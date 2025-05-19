import { Page, APIRequestContext, expect } from '@playwright/test';

/**
 * SitemapPage
 * Utility for interacting with the Netlify sitemap and verifying crawlability.
 */
export class SitemapPage {
  readonly page: Page;
  readonly request: APIRequestContext;
  sitemapUrl: string = 'https://www.netlify.com/sitemap.xml';

  constructor(page: Page, request: APIRequestContext) {
    this.page = page;
    this.request = request;
  }

  /**
   * Fetches and parses the sitemap, returning an array of URLs.
   */
  async getSitemapUrls(): Promise<string[]> {
    const resp = await this.request.get(this.sitemapUrl);
    expect(resp.status()).toBe(200);
    const xml = await resp.text();
    const urls = Array.from(xml.matchAll(/<loc>(.*?)<\/loc>/g)).map(m => m[1]);
    return urls;
  }

  /**
   * Checks if the given page has a robots meta tag with noindex.
   */
  async hasNoindexMeta(url: string): Promise<boolean> {
    await this.page.goto(url, { waitUntil: 'domcontentloaded' });
    const robots = await this.page.$('meta[name="robots"]');
    if (!robots) return false;
    const content = await robots.getAttribute('content');
    return content?.includes('noindex') ?? false;
  }

  /**
   * Checks if the given URL is accessible (status < 400).
   */
  async isUrlAccessible(url: string): Promise<boolean> {
    const resp = await this.request.get(url);
    return resp.status() < 400;
  }
}