import { test as baseTest } from '@playwright/test'
import { HomePage } from '../pages/HomePage'

type CustomFixtures = {
	homePage: HomePage
}

export const test = baseTest.extend<CustomFixtures>({
	homePage: async ({ page }, use) => {
		await use(new HomePage(page))
	}
})
