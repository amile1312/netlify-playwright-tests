import { test } from '../fixtures/site.fixture'

test.describe('Lead Capture Newsletter Form', () => {
	const testData = JSON.parse(JSON.stringify(require('../data/email.json')))

	test('should be present and function with valid data', async ({
		homePage
	}) => {
		await homePage.goto()
		await homePage.isFormVisible()
		await homePage.fillSubscriptionEmail(testData.email)
		await homePage.subscribeToNewsletter()
		await homePage.subscriptionConfirmed()
	})

	test('should validate and reject invalid email', async ({ homePage }) => {
		await homePage.goto()
		await homePage.isFormVisible()
		const invalidEmailResponseBody = await homePage.waitForEmailValidationResponse(
			homePage.emailCheckEndpoint,
			testData.invalidEmail,
			'POST'
		)
		await homePage.assertInvalidEmailResponse(invalidEmailResponseBody, testData.invalidEmail)
	})
})
