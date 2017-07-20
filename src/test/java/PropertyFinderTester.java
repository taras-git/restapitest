import com.jayway.restassured.http.ContentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.jayway.restassured.RestAssured.given;
import static junit.framework.TestCase.assertTrue;


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
    public void testPropertyFiderLeastExpensive() throws IOException {

        List<Appartament> appartamentList = new ArrayList<>();
        List<Appartament> appartamentListFiltered;

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
            Appartament app = new Appartament();
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
                .sorted(Comparator.comparing(Appartament::getPrice))
                .collect(Collectors.toList());

        // print the results
        for (Appartament a : appartamentListFiltered) {
            System.out.println(a.getAddress());
            System.out.println(a.getPrice());
        }

        // check if the last element of the filtered list has at least 2 bedrooms
        Appartament last = appartamentListFiltered.stream()
                .reduce((a, b) -> b)
                .orElse(null);

        assertTrue(last.getBedrooms() >= 2);

        System.out.println("LAST appartment has bedrooms: " + last.getBedrooms());
    }


    class Appartament {
        private Integer bedrooms;
        private Integer price;
        private String address;

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddress() {
            return address;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public Integer getBedrooms() {
            return bedrooms;
        }

        public void setBedrooms(Integer bedrooms) {
            this.bedrooms = bedrooms;
        }
    }

}