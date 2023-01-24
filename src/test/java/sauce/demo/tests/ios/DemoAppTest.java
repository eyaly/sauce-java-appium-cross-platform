package sauce.demo.tests.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.joda.time.DateTime;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;


import static sauce.demo.helpers.Constants.region;

public class DemoAppTest {

    private String SAUCE_EU_URL = "https://ondemand.eu-central-1.saucelabs.com/wd/hub";
    private String SAUCE_US_URL = "https://ondemand.us-west-1.saucelabs.com/wd/hub";

    private static ThreadLocal<IOSDriver> iosDriver = new ThreadLocal<IOSDriver>();

    // Locators
    // product item
    AppiumBy.ByAccessibilityId productBackPack = new AppiumBy.ByAccessibilityId("Sauce Lab Back Packs");
    AppiumBy.ByAccessibilityId productsTitle = new AppiumBy.ByAccessibilityId("Products");

    AppiumBy.ByAccessibilityId productDetailsScreen = new AppiumBy.ByAccessibilityId("ProductDetails-screen");
    AppiumBy.ByAccessibilityId addToCart = new AppiumBy.ByAccessibilityId("Add To Cart");
    By.ByXPath CartOneItem = new AppiumBy.ByXPath("//XCUIElementTypeStaticText[@name=\"1\"]");

    @BeforeMethod
    public void setup(Method method) throws MalformedURLException {

        String methodName = method.getName();
        System.out.println("*** BeforeMethod hook. Running method " + methodName + " ***");
        URL url;

        switch (region) {
            case "us":
                System.out.println("region is us");
                url = new URL(SAUCE_US_URL);
                break;
            case "eu":
            default:
                System.out.println("region is eu");
                url = new URL(SAUCE_EU_URL);
                break;
        }

        MutableCapabilities capabilities = new MutableCapabilities();
        MutableCapabilities sauceOptions = new MutableCapabilities();

        // Get the capabilities from the xml file
        String deviceName = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("deviceName");
        String platformVersion = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("platformVersion");
        String appName = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("app");
        String rdc = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("rdc");

        // For all capabilities please check
        // http://appium.io/docs/en/writing-running-appium/caps/#general-capabilities
        // https://docs.saucelabs.com/dev/test-configuration-options/#mobile-appium-capabilities
        // Use the platform configuration https://saucelabs.com/platform/platform-configurator#/
        // to find the simulators/real device names, OS versions and appium versions you can use for your testings

        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("automationName", "XCuiTest");
        capabilities.setCapability("appium:deviceName", deviceName == null ? "iPhone.*" : deviceName);
        capabilities.setCapability("appium:platformVersion", platformVersion == null ? "14" : platformVersion);
        capabilities.setCapability("appium:app", "storage:filename=" + appName );

        if (rdc.equals("true")) {
            sauceOptions.setCapability("resigningEnabled", true);
            sauceOptions.setCapability("sauceLabsNetworkCaptureEnabled", true);
        }
        // Sauce capabilities
        sauceOptions.setCapability("name", methodName);
        DateTime dt = new DateTime();
        sauceOptions.setCapability("build", "RDC Native Simple Example: build-" + dt.hourOfDay().getAsText() + "-" + dt.minuteOfHour().getAsText());
        sauceOptions.setCapability("username", System.getenv("SAUCE_USERNAME"));
        sauceOptions.setCapability("accessKey", System.getenv("SAUCE_ACCESS_KEY"));
        capabilities.setCapability("sauce:options", sauceOptions);

        try {
            iosDriver.set(new IOSDriver(url, capabilities));
        } catch (Exception e) {
            System.out.println("*** Problem to create the iOS driver " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @AfterMethod
    public void teardown(ITestResult result) {
        System.out.println("Sauce - AfterMethod hook");
        ((JavascriptExecutor)getDriver()).executeScript("sauce:job-result=" + (result.isSuccess() ? "passed" : "failed"));
        getDriver().quit();
    }

    public  IOSDriver getDriver() {
        return iosDriver.get();
    }

    @Test
    public void addProductToCart() {

        IOSDriver driver = getDriver();

        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(productsTitle));

        // Select product
        System.out.println("Sauce - Start selectProduct test");
        driver.findElement(productBackPack).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(productDetailsScreen));

        // Add to Cart
        driver.findElement(addToCart).click();

        WebElement itemInCart = getItemInTheCart();
        Assert.assertTrue(itemInCart !=null);
        // For the video
        waiting(2);

    }

    public WebElement getItemInTheCart() {

        WebElement itemInTheCart = null;
        //wait for the product field to be visible and store that element into a variable
        try {
            itemInTheCart = getDriver().findElement(CartOneItem);
        } catch (TimeoutException e){
            return null;
        }
        return itemInTheCart;
    }

    public void waiting(int sec){
        try
        {
            Thread.sleep(sec*1000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }


}