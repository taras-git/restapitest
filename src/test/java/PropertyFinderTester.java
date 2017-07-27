import com.jayway.restassured.http.ContentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.jayway.restassured.RestAssured.given;
import static junit.framework.TestCase.assertTrue;

import utils.CSVUtils;
import data.Appartment;

public class PropertyFinderTester {

    @Test
    public void testPropertyFinderIsUp() {
        given()
                .when()
                .get("https://www.propertyfinder.ae/search?l=50&q=&c=2&t=&rp=y&pf=&pt=&bf=2&bt=&af=&at=&fu=0&kw=")
                .then()
                .statusCode(200)
                .and()
                .contentType(ContentType.HTML);
    }


    @Test
    public void testPropertyFinderLeastExpensive() throws IOException {

        List<Appartment> appartamentList = new ArrayList<>();
        List<Appartment> appartamentListFiltered;

        // get html code of 1st page of property finder site
        Document page = Jsoup.connect("https://www.propertyfinder.ae/search?l=&q=&c=2&t=&rp=y&pf=&pt=&bf=&bt=&af=&at=&fu=0&kw=").get();
        // get appartments info from the 1st page
        Elements elementsAppInfo = page.select("li.gtm-impression.premium");

        // get appartments info from some next pages
        for (int i = 2; i < 4; i++) {
            page = Jsoup.connect("https://www.propertyfinder.ae/search?l=&q=&c=2&t=&rp=y&pf=&pt=&bf=&bt=&af=&at=&fu=0&kw=&page=" + i).get();
            Elements newAppInfo = page.select("li.gtm-impression.premium");
            for (Element info : newAppInfo) {
                elementsAppInfo.add(info);
            }
        }

        for (Element e : elementsAppInfo) {
            // extract address
            String address = e.select("div.location-tree").text();
            // extract number of bedrooms
            String bedrooms = e.select("div.property-details > span:nth-child(2)").text();
            // convert number of bedrooms to integer
            Integer bedroomsNum = Integer.parseInt(bedrooms);
            // extract price for the appartment
            Integer price = Integer.parseInt(
                    e.select("span.price > span.val")
                            .text()
                            .replace(",", ""));

            // create new appartment object
            Appartment app = new Appartment();
            app.setBedrooms(bedroomsNum);
            app.setPrice(price);
            app.setAddress(address);

            // populate list with the new appartment
            appartamentList.add(app);
        }

        // filter list of appartment for: address has word "Marina"
        // filter resulted list of appartment for: bedrooms at least 2
        // sort the resulted list by price
        // populate appartamentListFiltered list with the results
        appartamentListFiltered = appartamentList.stream()
                .filter(addr -> addr.getAddress().contains("Marina"))
                .filter(bdr -> bdr.getBedrooms() >= 2)
                .sorted(Comparator.comparing(Appartment::getPrice))
                .collect(Collectors.toList());

        // print the results
        for (Appartment a : appartamentListFiltered) {
            System.out.println(a.getAddress());
            System.out.println(a.getPrice());
        }

        // check if the last element of the filtered list has at least 2 bedrooms
        Appartment last = appartamentListFiltered.stream()
                .reduce((a, b) -> b)
                .orElse(null);

        assertTrue(last.getBedrooms() >= 2);

        System.out.println("LAST appartment has bedrooms: " + last.getBedrooms());
    }

    @Test
    public void testPropertyFinderVilla3Beds7Beds() throws IOException {

        List<Appartment> apartmentList = new ArrayList<>();
        List<Appartment> appartmentListFiltered;

        String villaToBuy = "https://www.propertyfinder.qa/search?l=&q=&c=1&t=35&pf=&pt=&bf=&bt=&af=&at=&kw=";
        String appInfoCssLocator = ".listing-content";

        // get html code of 1st htmlCodePage of property finder site
        Document htmlCodePage = Jsoup.connect(villaToBuy).get();
        // get appartments info from the 1st htmlCodePage
        Elements elementsAppInfo = htmlCodePage.select(appInfoCssLocator);

        // get appartments info from some next pages
        for (int i = 2; i < 4; i++) {
            htmlCodePage = Jsoup.connect(villaToBuy + "&htmlCodePage=" + i).get();
            Elements newAppInfo = htmlCodePage.select(appInfoCssLocator);
            for (Element info : newAppInfo) {
                elementsAppInfo.add(info);
            }
        }

        String addressCssLocator        = "div.location-tree";
        String bedroomsCssLocator       = "div.property-details > span:nth-child(2)";
        String priceCssLocator          = "span.price > span.val";
        String currencyCssLocator       = ".currency";

        // iterate over results to crate proper Appartment objects
        for (Element e : elementsAppInfo) {
            // extract address
            String address = e.select(addressCssLocator).text();
            // extract number of bedrooms
            String bedrooms = e.select(bedroomsCssLocator).text().replaceAll("\\D+", "");
            // convert number of bedrooms to integer
            Integer bedroomsNum = Integer.parseInt(bedrooms);
            // extract price for the appartment
            Integer price = Integer.parseInt(
                    e.select(priceCssLocator)
                            .text()
                            .replace(",", ""));
            // get currency
            String currency = e.select(currencyCssLocator).text();

            // create new appartment object
            Appartment app = new Appartment();
            app.setBedrooms(bedroomsNum);
            app.setPrice(price);
            app.setAddress(address);
            app.setCurrency(currency);

            // populate list with the new appartment
            apartmentList.add(app);
        }

        // filter list of appartment for: address has word "The Pearl"
        // filter resulted list of appartment for: bedrooms minimum 3 and maximum 7
        // sort the resulted list by price
        // populate appartmentListFiltered list with the results
        appartmentListFiltered = apartmentList.stream()
                .filter(addr -> addr.getAddress().contains("The Pearl"))
                .filter(bdr -> (bdr.getBedrooms() >= 3) && (bdr.getBedrooms() <= 7))
                .sorted(Comparator.comparing(Appartment::getPrice).reversed())
                .collect(Collectors.toList());

        // generate csv file
        String csvFile = "villa.csv";
        FileWriter writer = new FileWriter(csvFile);
        // print the results and generate csv file
        for (Appartment a : appartmentListFiltered) {
            System.out.println(a.getAddress());
            System.out.println(a.getPrice());
            System.out.println(a.getCurrency());
            CSVUtils.writeLine(writer, Arrays.asList(a.getAddress(),
                    a.getPrice().toString() + " " + a.getCurrency()), '-');
        }
        writer.flush();
        writer.close();
    }

}