# Netlify End 2 End Tests

This repository contains a proof of concept (POC) for end-to-end testing using Playwright for the Netlify website.

## Prerequisites

- Node.js (>= 14.x)
- npm (>= 6.x)
- Playwright (>= 1.50.1)

## Installation

1. Clone the repository:

   ```sh
   git clone https://github.com/amile1312/netlify-playwright-tests.git
   cd netlify-playwright-tests
   ```

2. Install the dependencies:

   ```sh
   npm install
   ```

## Repository Structure

```bash
├── data/                 # Test data in JSON format
├── fixtures/           # Custom test fixtures
├── pages/              # Page Object Models
├── tests/               # Test files
└── utils/               # Utility functions
```

## Page Object Model (POM) Usage

The repository follows the Page Object Model (POM) pattern to organize the code for different pages. Each page class contains methods to interact with the page elements.

Example:

```ts
import { Page } from '@playwright/test'

export class PageUtils {
	static async getAllLinks(page: Page): Promise<string[]> {
		return page.$$eval('a[href]', (as) =>
			as.map((a) => (a as HTMLAnchorElement).href)
		)
	}
}
```

## Fixtures

Custom fixtures are defined to provide reusable components.

Example:

```ts
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
```

## Data in JSON

Test data is stored in JSON files in the `data/` directory. These files are imported and used in the test specifications.

Example:

```json
{
	"email": "acam1312@gmail.com",
	"invalidEmail": "testuser@example.c"
}
```

## Running Tests

To run the tests, use the following command:

```sh
npx playwright test
```

To run the tests through Playwright test runner (GUI mode) - (you may need to install playwright globally, `npm install --global playwright`), use the following command:

```sh
npm run test-ui
```

To generate a test report:

```sh
npx playwright show-report
```

## Architecture

1. Page Objects
   - Encapsulated UI interactions
   - Reusable components
   - Type safety with TypeScript
2. Test Data & Configuration
   - Placed in JSON files
   - Separates test data from test logic
   - Custom Fixtures
3. Shared test context
   - Automatic page object initialization
   - Clean test syntax

## Contributing

- Create a feature branch
- Add or modify tests
- Update documentation if needed
- Submit a pull request
