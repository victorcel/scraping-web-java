/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package consultaspaginas;

/**
 *
 * @author VBarrera
 */
import java.io.File;
import java.io.IOException;
import java.sql.Driver;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class ConsultasPaginas {

    FirefoxOptions options = new FirefoxOptions()
            .addPreference("network.proxy.type", 0)
            .addPreference("javascript.enabled", true)
            .addPreference("permissions.default.image", 3)
            .addPreference("permissions.default.stylesheet", 2) .addArguments("--headless")
            ;
    WebDriver driver = new FirefoxDriver(options);

    WebDriverWait myWait = new WebDriverWait(driver, 3);
    JavascriptExecutor executor = (JavascriptExecutor) driver;

    String baseUrlPolicia = "https://antecedentes.policia.gov.co:7005/WebJudicial/";
    String baseUrlOfac = "http://sanctionssearch.ofac.treas.gov/";
    String baseUrlProcuraduria = "https://www.procuraduria.gov.co/CertWEB/Certificado.aspx?tpo=1";

    public ConsultasPaginas() {

        driver.manage().window().setSize(new Dimension(400, 600));

//
//        // launch Fire fox and direct it to the Base URL
        driver.get(baseUrlPolicia);
    }

    String policia(String identidad) {
        String respuesta = null;
        String cedulaInput = null;
        try {
            cedulaInput = identidad/*JOptionPane.showInputDialog(
                    null,
                    new JLabel("Escriba numero de identificacion", JLabel.LEFT),
                    "Numero Identificacion", JOptionPane.QUESTION_MESSAGE)*/;

            WebElement aceptar = driver.findElement(By.id("aceptaOption:0"));
            executor.executeScript("arguments[0].click()", aceptar);
            Thread.sleep(2000);
            WebElement continuar = driver.findElement(By.id("continuarBtn"));
            executor.executeScript("arguments[0].click()", continuar);
            Thread.sleep(5000);
            myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cedulaInput")));
            driver.findElement(By.id("cedulaInput")).sendKeys(cedulaInput);
            myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("capimg")));
            WebElement imagen = driver.findElement(By.id("capimg"));
            File screenshotFile = ((TakesScreenshot) imagen).getScreenshotAs(OutputType.FILE);

            Random r = new Random();
            int aleatorio = r.nextInt(10000) + 1;  // Entre 0 y 5, más 1.
            FileUtils.copyFile(screenshotFile, new File("C://captcha/" + aleatorio + ".png"));
            ImageIcon icon = new ImageIcon(("C:\\captcha\\" + aleatorio + ".png"));
            String captcha = JOptionPane.showInputDialog(
                    null,
                    new JLabel("", icon, JLabel.LEFT),
                    "Captcha", JOptionPane.QUESTION_MESSAGE);
            driver.findElement(By.id("textcaptcha")).sendKeys(captcha);
            try {
                WebElement enviar = driver.findElement(By.id("j_idt20"));
                executor.executeScript("arguments[0].click()", enviar);
                Thread.sleep(3000);
                myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form:mensajeCiudadano")));
                WebElement detalle = driver.findElement(By.id("form:mensajeCiudadano"));
                List<WebElement> detalles = detalle.findElements(By.tagName("b"));
                if (detalles.size() == 4) {
                    respuesta = (detalles.get(1).getText());
                } else {
                    respuesta = (detalles.get(2).getText() + " " + detalles.get(3).getText());
                }
                Thread.sleep(2000);
                WebElement atras = driver.findElement(By.id("form:bt02"));
                executor.executeScript("arguments[0].click()", atras);
            } catch (InterruptedException ex) {

                JOptionPane.showMessageDialog(null, ex.getMessage());
                cerrar();
            }
            return respuesta;
//            }
        } catch (InterruptedException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            cerrar();
        } catch (NoSuchElementException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            cerrar();
        } catch (TimeoutException ex) {
            //System.out.println("policia " + te.getMessage());
//            WebElement error = driver.findElement(By.xpath("/html/body/section[2]/form/div/div/div[1]/div/ul/li/span"));
//            JOptionPane.showMessageDialog(null, error.getText());
//            return error.getText();
            JOptionPane.showMessageDialog(null, ex.getMessage());
            cerrar();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            cerrar();
        }
        return null;

    }

    String ofac(String identidad) {
        try {
            driver.get(baseUrlOfac);
            Thread.sleep(2000);
            driver.findElement(By.id("ctl00_MainContent_txtID")).sendKeys(identidad);
            Thread.sleep(2000);
            myWait.until(ExpectedConditions.visibilityOfElementLocated(By.name("ctl00$MainContent$btnSearch")));
            driver.findElement(By.name("ctl00$MainContent$btnSearch")).click();
            WebElement detalles = driver.findElement(By.id("gvSearchResults"));
            myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnDetails")));

            return detalles.findElement(By.id("btnDetails")).getText();

        } catch (InterruptedException ex) {
            cerrar();
            return ex.getMessage();
        } catch (NoSuchElementException ex) {

            return "EL NÚMERO DE IDENTIFICACIÓN INGRESADO NO SE ENCUENTRA REGISTRADO EN EL SISTEMA.";
        }
        //return null;
    }

    String procuraduria(String identidad) {
        try {
            driver.get(baseUrlProcuraduria);
            Select tipoId = new Select(driver.findElement(By.id("ddlTipoID")));
            tipoId.selectByIndex(1);

            driver.findElement(By.id("txtNumID")).sendKeys(identidad);
            myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lblPregunta")));
            WebElement pregunta = driver.findElement(By.id("lblPregunta"));

            String respuesta = JOptionPane.showInputDialog(
                    null,
                    new JLabel(pregunta.getText(), JLabel.LEFT),
                    "Captcha", JOptionPane.QUESTION_MESSAGE);
            driver.findElement(By.id("txtRespuestaPregunta")).sendKeys(respuesta);
            Thread.sleep(1000);
            driver.findElement(By.name("btnConsultar")).click();
            WebElement detalles = driver.findElement(By.id("divSec"));
            List<WebElement> datos = detalles.findElements(By.tagName("span"));
            if (datos.size() > 1) {

                return (datos.get(0).getText() + " " + datos.get(1).getText() + " " + datos.get(2).getText() + " " + datos.get(3).getText() + " - " + detalles.findElement(By.tagName("h2")).getText());
            } else {

                return "EL NÚMERO DE IDENTIFICACIÓN INGRESADO NO SE ENCUENTRA REGISTRADO EN EL SISTEMA.";
            }

        } catch (NoSuchElementException e) {
            cerrar();
            System.err.println("" + e.getMessage());
        } catch (InterruptedException ex) {
            cerrar();
            Logger.getLogger(ConsultasPaginas.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            driver.get(baseUrlPolicia);
        }

        return "LA RESPUESTA ERRADA - REPETIR CONSULTA";
    }

    void cerrar() {
        try {
            driver.close();
            Runtime.getRuntime().exec("TASKKILL /F /IM geckodriver.exe");
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(ConsultasPaginas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    String captcha(String cedulaInput) {
//        String captcha = null;
//        try {
//
//            myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("capimg")));
//            WebElement imagen = driver.findElement(By.id("capimg"));
//            File screenshotFile = ((TakesScreenshot) imagen).getScreenshotAs(OutputType.FILE);
//            FileUtils.copyFile(screenshotFile, new File("C://Python27/buscar/captcha/" + cedulaInput + ".png"));
//            ImageIcon icon = new ImageIcon(("C:\\Python27\\buscar\\captcha\\" + cedulaInput + ".png"));
//            captcha = JOptionPane.showInputDialog(
//                    null,
//                    new JLabel("", icon, JLabel.LEFT),
//                    "Captcha", JOptionPane.QUESTION_MESSAGE);
//
//        } catch (IOException ex) {
//            System.err.println("captcha " + ex.getMessage());
//        }
//        return captcha;
//    }
//    String enviar() {
//        String respuesta = null;
//        try {
//
//            WebElement enviar = driver.findElement(By.id("j_idt20"));
//            executor.executeScript("arguments[0].click()", enviar);
//            myWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form:mensajeCiudadano")));
//            WebElement detalle = driver.findElement(By.id("form:mensajeCiudadano"));
//            List<WebElement> detalles = detalle.findElements(By.tagName("b"));
//            if (detalles.size() == 4) {
//                respuesta = (detalles.get(1).getText());
//            } else {
//                respuesta = (detalles.get(2).getText() + " " + detalles.get(3).getText());
//            }
//            Thread.sleep(2000);
//            WebElement atras = driver.findElement(By.id("form:bt02"));
//            executor.executeScript("arguments[0].click()", atras);
//            
//        } catch (InterruptedException ex) {
//            Logger.getLogger(ConsultasPaginas.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return respuesta;
//    }
}
