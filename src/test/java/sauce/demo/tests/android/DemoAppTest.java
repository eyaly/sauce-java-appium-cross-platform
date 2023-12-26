package sauce.demo.tests.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobilePlatform;
import org.joda.time.DateTime;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
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
import java.util.Arrays;
import java.util.List;


import static java.lang.System.out;
import static sauce.demo.helpers.Constants.*;

public class DemoAppTest {
    protected static ThreadLocal<AndroidDriver> driver = new ThreadLocal<AndroidDriver>();

    // Locators
    By productsTitle = By.id("com.saucelabs.mydemoapp.android:id/productTV");

    By productsItems = By.id("com.saucelabs.mydemoapp.android:id/productIV");

    By productDetailsScreen =  AppiumBy.accessibilityId("Container for fragments");
    By addToCart =  AppiumBy.accessibilityId("Tap to add product to cart");

    By CartOneItem = By.id("com.saucelabs.mydemoapp.android:id/cartTV");

    @BeforeMethod
    public void setup(Method method) throws MalformedURLException {

        out.println("Sauce Android Native App  - Before hook");

        URL url;
        MutableCapabilities capabilities = new MutableCapabilities();
        String deviceName = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("deviceName");
        String platformVersion = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("platformVersion");
        String appName = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("app");
        String rdc = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("rdc");
        String appiumVersion = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("appiumVersion");
        String appWaitActivity = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("appWaitActivity");

        if (env.equals("saucelabs")) {
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

            MutableCapabilities sauceOptions = new MutableCapabilities();

            // For all capabilities please check
            // http://appium.io/docs/en/writing-running-appium/caps/#general-capabilities
            // https://docs.saucelabs.com/dev/test-configuration-options/#mobile-appium-capabilities
            // Use the platform configuration https://saucelabs.com/platform/platform-configurator#/
            // to find the simulators/real device names, OS versions and appium versions you can use for your testings
            capabilities.setCapability("platformName", MobilePlatform.ANDROID);
            capabilities.setCapability("appium:automationName", AutomationName.ANDROID_UIAUTOMATOR2);

            capabilities.setCapability("appium:deviceName", deviceName == null ? "Samsung.*" : deviceName);
            capabilities.setCapability("appium:platformVersion", platformVersion == null ? "12" : platformVersion);
            capabilities.setCapability("appium:app", "storage:filename=" + appName);

            if (rdc.equals("true")) {
                sauceOptions.setCapability("resigningEnabled", true);
                sauceOptions.setCapability("sauceLabsNetworkCaptureEnabled", true);
            } else {
                capabilities.setCapability("appium:appWaitActivity", appWaitActivity);
            }
//        sauceOptions.setCapability("build", "myApp-job-1");
//        List<String> tags = Arrays.asList("sauceDemo_android", "android", "Demo");
//        sauceOptions.setCapability("tags", tags);
            sauceOptions.setCapability("name", method.getName());
            DateTime dt = new DateTime();
            sauceOptions.setCapability("build", "RDC Native Simple Example: build-" + dt.hourOfDay().getAsText() + "-" + dt.minuteOfHour().getAsText());
            sauceOptions.setCapability("username", SAUCE_USERNAME);
            sauceOptions.setCapability("accessKey", SAUCE_ACCESS_KEY);
            if (appiumVersion != null)
                sauceOptions.setCapability("appiumVersion", appiumVersion);
            capabilities.setCapability("sauce:options", sauceOptions);

            try {
                driver.set(new AndroidDriver(url, capabilities));
            } catch (Exception e) {
                out.println("*** Problem to create the Android driver " + e.getMessage());
                throw new RuntimeException(e);
            }
        } else { // run on local appium server
            System.out.println("Run on local Appium Server");
            capabilities.setCapability("platformName", "Android");
            capabilities.setCapability("appium:deviceName", deviceName);
            capabilities.setCapability("appium:app", "/Users/eyalyovel/Documents/github/sauce-java-appium-cross-platform/src/test/java/sauce/demo/apps/" + appName);
            capabilities.setCapability("appium:automationName", "UiAutomator2");
            url = new URL("http://localhost:4723/");

        }

    }

    @AfterMethod
    public void teardown(ITestResult result) {
        out.println("Sauce - AfterMethod hook");
        try {
            ((JavascriptExecutor) getDriver()).executeScript("sauce:job-result=" + (result.isSuccess() ? "passed" : "failed"));
        } finally {
            out.println("Sauce - release driver");
            getDriver().quit();
        }
    }

    public  AndroidDriver getDriver() {
        return driver.get();
    }

    @Test
    public void addProductToCart() {

        System.out.println("Sauce - Start selectProduct test");
        AndroidDriver driver = getDriver();

        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(productsTitle));

        // Select product
        List<WebElement> products = driver.findElements(productsItems);
        products.get(0).click();
        WebDriverWait wait2 = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
        WebElement productDetailsElement = wait2.until(ExpectedConditions.visibilityOfElementLocated(productDetailsScreen));


        // Add to Cart
        WebElement addToCartElm = null;
        try {
            addToCartElm = driver.findElement(addToCart);
        } catch (NoSuchElementException ex){
            // scroll
            scrollElementUP(productDetailsElement);
            addToCartElm = driver.findElement(addToCart);
        }

        addToCartElm.click();
        WebElement itemInCart = getItemInTheCart();
        Assert.assertTrue(itemInCart !=null);
        // For the video
        waiting(3);

    }

    @Test
    public void selectProductBikeLight() {
        System.out.println("Sauce - Start selectProductBikeLight test");

        // The item will crash the app
        AndroidDriver driver = getDriver();

        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(productsTitle));

        // Select product
        List<WebElement> products = driver.findElements(productsItems);
        products.get(1).click();
        WebDriverWait wait2 = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(productDetailsScreen));

        try
        {
            Thread.sleep(3000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }

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

    public void scrollElementUP(WebElement el) {
        AndroidDriver driver = getDriver();
        // 1. The rectangle of the element to scroll
        Rectangle rect = el.getRect();

        // 2. Determine the x and y position of initial touch
        // we scroll up and the x doesn't change
        int centerX = rect.x + (int)(rect.width /2);
        // The starting Y position
        int startY = rect.y + (int)(rect.height*0.9);
        // The end Y position
        int endY = rect.y;

        // 3. swipe: https://appium.io/docs/en/commands/interactions/actions/
        // finger
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tapPoint = new Sequence(finger, 1);
        // Move finger into start position
        tapPoint.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), centerX, startY));
        // Finger comes down into context with screen
        tapPoint.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        // Pause for a little bit
        tapPoint.addAction(new Pause(finger, Duration.ofMillis(100)));
        // Finger move to end position
        tapPoint.addAction(finger.createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(), centerX, endY));
        // Finger gets up, off the screen
        tapPoint.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        // Perform the scroll
        driver.perform(Arrays.asList(tapPoint));

        // always allow scroll action to complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
