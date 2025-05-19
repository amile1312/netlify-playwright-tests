import { Page, Locator, expect } from '@playwright/test';

export class HomePage {
  readonly page: Page;
  readonly rejectCookies: Locator;
  readonly newsletterForm: Locator;
  readonly emailInput: Locator;
  readonly subscribeButton: Locator;
  readonly feedbackMsg: Locator;
  readonly thanksForSub: Locator;
  readonly viewOurDocumentationButton: Locator;
  readonly ChatInOurCommunityButton: Locator;
  readonly lookingForward: Locator;
  readonly emailCheckEndpoint: string;

  constructor(page: Page) {
    this.page = page;
    this.rejectCookies = page.locator('button[id="onetrust-reject-all-handler"]');
    this.newsletterForm = page.locator('section[class="newsletter-form | l-stack l-stack-small l-center"]');
    this.emailInput = this.newsletterForm.locator('input[type="email"]');
    this.subscribeButton = this.newsletterForm.locator('input[type="submit"]');
    this.feedbackMsg = this.newsletterForm.locator('[aria-live], .feedback, .error, .success');
    this.thanksForSub = page.getByRole('heading', { name: 'Thank you for signing up!' });
    this.viewOurDocumentationButton = page.getByRole('link', { name: 'View our documentation' });
    this.ChatInOurCommunityButton = page.getByRole('link', { name: 'Chat in our Community' });
    this.lookingForward = page.getByText('We are looking forward to keep you posted with updates and interesting articles.');
    this.emailCheckEndpoint = 'https://forms.hsforms.com/emailcheck/v1/json-ext?hs_static_app=forms-embed&hs_static_app_version=1.8323&X-HubSpot-Static-App-Info=forms-embed-1.8323&portalId=7477936&formId=52611e5e-cc55-4960-bf4a-a2adb36291f6&includeFreemailSuggestions=true';
  }

  async goto() {
    await this.page.goto('https://www.netlify.com/');
    await expect(this.rejectCookies).toBeVisible();
    await this.rejectCookies.click();
  }

  async isFormVisible() {
    await expect(this.newsletterForm).toBeVisible();
  }

  async expectFeedback(contains: string) {
    await expect(this.feedbackMsg).toContainText(contains, { timeout: 5000 });
  }

  async subscribeToNewsletter(email: string) {
    await this.emailInput.fill(email);
    await this.subscribeButton.click();
  }

  async subscriptionConfirmed() {
    await expect(this.thanksForSub).toBeVisible();
    await expect(this.lookingForward).toBeVisible();
    await expect(this.viewOurDocumentationButton).toBeEnabled();
    await expect(this.ChatInOurCommunityButton).toBeEnabled();
  }

    /**
   * Inputs invalid email for the newsletter form and waits for the network response.
   * @param email The email to input.
   * @param enpointUrl Part of the endpoint URL to identify the request (e.g. '/newsletter/').
   * @returns The parsed response body.
   */
  async submitNewsletterAndWaitForResponse(enpointUrl: string, email: string, method: string) {
    const [response] = await Promise.all([
      this.page.waitForResponse(resp =>
        resp.url().includes(enpointUrl) && resp.request().method() === method
      ),
      email
    ]);
    return response.json();
  }

  async checkInvalidEmail() {
    const testData = JSON.parse(JSON.stringify(require('../data/email.json')))
    const responseBody = await this.submitNewsletterAndWaitForResponse(
			this.emailCheckEndpoint,
			testData.invalidEmail,
			'POST'
		)
    expect(responseBody).toMatchObject({
			success: false,
			email: testData.invalidEmail,
			emailShouldResubscribe: false,
			emailFree: false,
			emailSuggestion: testData.invalidEmail + 'a',
			isRateLimited: null
		})
  }
}