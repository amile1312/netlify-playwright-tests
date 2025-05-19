import { test, expect } from '@playwright/test';

test.describe('Sitemap and Crawlability', () => {
  let sitemapUrls: string[] = [];

  test('sitemap.xml exists and contains URLs', async ({ request }) => {
    const resp = await request.get('https://www.netlify.com/sitemap.xml');
    expect(resp.status()).toBe(200);
    const xml = await resp.text();
    expect(xml).toContain('<urlset');
    sitemapUrls = Array.from(xml.matchAll(/<loc>(.*?)<\/loc>/g)).map(m => m[1]);
    expect(sitemapUrls.length).toBeGreaterThan(0);
  });

  test('all URLs in sitemap are accessible and crawlable', async ({ request, page }) => {
    // Limit for demo; remove or adjust for full sitemap
    for (const url of sitemapUrls.slice(0, 10)) {
      const resp = await request.get(url);
      expect(resp.status(), `URL: ${url}`).toBeLessThan(400);

      await page.goto(url, { waitUntil: 'domcontentloaded' });
      const robots = await page.$('meta[name="robots"]');
      if (robots) {
        const content = await robots.getAttribute('content');
        expect(content).not.toContain('noindex');
      }
    }
  });
});