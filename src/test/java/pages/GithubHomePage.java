package pages;

import manager.BrowserManager;

public class GithubHomePage extends BasePage {

    private final String pricingLink = "//nav[@aria-label='Global']//a[contains(text(),'Pricing')]";
    private final String pricingTitle = "//h1[@class='h2-mktg']";

    public GithubHomePage(BrowserManager browserManager) {
        super(browserManager);
    }

    public void clickPricingLink() {
        click(pricingLink);
    }

    public String getPricingTitle() {
        return getText(pricingTitle);
    }
}
