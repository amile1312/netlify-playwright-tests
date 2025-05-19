import { Page } from '@playwright/test'

export class PageUtils {
	static async getAllLinks(page: Page): Promise<string[]> {
		return page.$$eval('a[href]', (as) =>
			as.map((a) => (a as HTMLAnchorElement).href)
		)
	}
}