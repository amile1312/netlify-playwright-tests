import { test, expect } from '@playwright/test'
import { PageUtils } from '../utils/PageUtils'

test.describe('404 Link Verification', () => {
	const checkedPages = JSON.parse(
		JSON.stringify(require('../data/checkedPages.json'))
	)

	for (const pageUrl of checkedPages) {
		test(`All links on ${pageUrl} do not lead to 404`, async ({
			page,
			request
		}) => {
			await page.goto(pageUrl)
			const links = (await PageUtils.getAllLinks(page)).filter((url) =>
				url.startsWith('https://www.netlify.com/')
			)
			for (const link of links) {
				const resp = await request.get(link)
				expect(resp.status(), `Broken link: ${link}`).not.toBe(404)
			}
		})
	}
})
