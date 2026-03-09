import { test, expect } from '@playwright/test';

// ---------------------------------------------------------------------------
// Home Page
// ---------------------------------------------------------------------------

test.describe('Home Page', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('should load with correct title', async ({ page }) => {
    await expect(page).toHaveTitle('Harness Webapp');
  });

  test('should display the navbar with logo and nav links', async ({ page }) => {
    const logo = page.locator('img[alt="Harness logo"]');
    await expect(logo).toBeVisible();

    const homeLink = page.locator('a.nav-link', { hasText: 'Home' });
    await expect(homeLink).toBeVisible();

    const distLink = page.locator('a.nav-link', { hasText: 'Distribution' });
    await expect(distLink).toBeVisible();
  });

  test('should display service name, version, and last execution', async ({ page }) => {
    // The controller falls back to these defaults when backend is unreachable
    const serviceName = page.locator('h1.heading').first();
    await expect(serviceName).toBeVisible();

    const versionCard = page.locator('h1.heading', { hasText: 'Version' });
    await expect(versionCard).toBeVisible();

    const executionCard = page.locator('h1.heading', { hasText: 'Last Execution' });
    await expect(executionCard).toBeVisible();
  });

  test('should have a visible Check Release button', async ({ page }) => {
    const btn = page.locator('#checkReleaseBtn');
    await expect(btn).toBeVisible();
    await expect(btn).toHaveText('Check Release');
  });

  test('should display the footer with Powerpay logo', async ({ page }) => {
    const footerImg = page.locator('footer img[alt="Powerpay by Dayforce"]');
    await expect(footerImg).toBeVisible();
  });
});

// ---------------------------------------------------------------------------
// Navigation
// ---------------------------------------------------------------------------

test.describe('Navigation', () => {
  test('should navigate to Distribution page via nav link', async ({ page }) => {
    await page.goto('/');

    const distLink = page.locator('a.nav-link', { hasText: 'Distribution' });
    await distLink.click();

    await expect(page).toHaveURL(/\/distribution/);
    await expect(page).toHaveTitle('Distribution - Harness Webapp');
  });
});

// ---------------------------------------------------------------------------
// Distribution Page
// ---------------------------------------------------------------------------

test.describe('Distribution Page', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/distribution');
  });

  test('should load with correct title', async ({ page }) => {
    await expect(page).toHaveTitle('Distribution - Harness Webapp');
  });

  test('should display the chart canvas', async ({ page }) => {
    const canvas = page.locator('#distributionChart');
    await expect(canvas).toBeVisible();
  });

  test('should have play and refresh buttons', async ({ page }) => {
    const playBtn = page.locator('#runBtn');
    await expect(playBtn).toBeVisible();

    const refreshBtn = page.locator('#refreshBtn');
    await expect(refreshBtn).toBeVisible();
  });

  test('should display the navbar on distribution page', async ({ page }) => {
    const logo = page.locator('img[alt="Harness logo"]');
    await expect(logo).toBeVisible();
  });

  test('should display the footer on distribution page', async ({ page }) => {
    const footerImg = page.locator('footer img[alt="Powerpay by Dayforce"]');
    await expect(footerImg).toBeVisible();
  });
});
