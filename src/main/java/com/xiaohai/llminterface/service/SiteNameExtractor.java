package com.xiaohai.llminterface.service;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xiaoyuntao
 * @date 2025/04/15
 */
public class SiteNameExtractor {

    public static final ForkJoinPool TEST_POOL = new ForkJoinPool(10);

    public static final List<String> urls = List.of(
            "https://www.southernpackaginglp.com/blog/current-and-upcoming-industrial-packaging-trends?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://explodingtopics.com/blog/packaging-trends?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.hexcelpack.com/sustainable-packaging-blog/3-sustainable-packaging-trends?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.internationalpaper.com/resources/article/unboxing-2025s-sustainable-packaging-trends?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.futuremarketinsights.com/reports/bag-in-box-market",
            "https://www.towardspackaging.com/insights/packaging-market-sizing?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.esko.com/en/blog/sustainability-in-packaging-navigating-trends-challenges-2025?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.futuremarketinsights.com/reports/bags-market",
            "https://www.travelandleisure.com/style/best-mens-winter-jackets-and-coats",
            "https://www.statista.com/outlook/cmo/apparel/men-s-apparel/coats-jackets/worldwide?currency=USD&accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://explodingtopics.com/blog/packaging-trends?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.statista.com/outlook/cmo/apparel/men-s-apparel/coats-jackets/united-states?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.apetogentleman.com/mens-coat-jacket-trends/?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.prnewswire.com/news-releases/mens-coat-jacket-and-suit-market-to-grow-by-usd-20-78-billion-2024-2028-with-designer-demand-and-ai-redefining-the-market-landscape---technavio-302265101.html?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://shopbrumano.com/blogs/fashion/2025-mens-jacket-trends-complete-guide?srsltid=AfmBOooUg_Bvtz1TjR3fZJ0IpS6Y9SqYI7vA4XxBRBAHZuETOHwU1BLe&accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.researchandmarkets.com/report/mens-coat?srsltid=AfmBOopNNfcIBLpu_kUXqRJKKpRg6Ndo24wc9ZjfTSMw53dL7IlDFapX&accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.statista.com/topics/10572/outerwear-market-worldwide/?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.monitordaily.com/article-posts/market-outlook-2025-construction/?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.southeasternequip.com/construction-forecast-and-trends/?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.linkedin.cn/incareer/pulse/construction-machinery-market-analysis-2025-2032-projected-99-oytge?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www2.deloitte.com/us/en/insights/industry/engineering-and-construction/engineering-and-construction-industry-outlook.html?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.globenewswire.com/news-release/2025/02/20/3029862/28124/en/Construction-Equipment-Market-Global-Forecast-to-2030-with-Caterpillar-Komatsu-Hitachi-XCMG-and-Deere-Co-Dominating-the-194-75-Billion-Industry.html?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://dropship-spy.com/blog/top-digital-marketing-trends-for-2025-shopify-strategies-that-will-skyrocket-your-e-commerce-success?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.constructionbriefing.com/news/are-construction-equipment-sales-poised-to-bounce-back-in-2025/8049606.article?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread&zephr_sso_ott=2uNNWT",
            "https://www.globenewswire.com/news-release/2025/01/21/3012638/0/en/Heavy-Construction-Equipment-Market-Trends-Challenges-and-Growth-Opportunities-Exactitude-Consultancy.html?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.aem.org/news/ag-construction-equipment-markets-hope-for-muchneeded-momentum-in-2025?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.maintecx.com/resource-center/construction-equipment-market-outlook-2025-navigating-the-shift-from-plateau-to-growth?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.thebusinessresearchcompany.com/report/construction-machinery-global-market-report?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.mchpartsnyc.com/post/10-smart-ways-to-pick-construction-machinery-for-2025-projects?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.exinent.com/latest-shopify-development-trends-2025/?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://conceptltd.com/blog/top-ecommerce-trends-2025?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.forconstructionpros.com/equipment/earthmoving/article/22932641/nominations-now-open-contractors-top-50-new-products-2025?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.volvogroup.com/en/news-and-media/news/2025/jan/volvo-construction-equipment-unveils-brand-new-lineup-of-its-world-leading-range-of-articulated-haulers.html?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.shopify.com/blog/tiktok-marketing?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://medicalsalescollege.com/blog/medical-device-industry-trends/?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.futuremarketinsights.com/reports/hospital-consumables-market",
            "https://www2.deloitte.com/us/en/insights/industry/health-care/life-sciences-and-health-care-industry-outlooks/2025-life-sciences-executive-outlook.html?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.towardshealthcare.com/insights/healthcare-consumables-market-sizing?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.fastcompany.com/91269635/medical-devices-most-innovative-companies-2025?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.globenewswire.com/news-release/2025/02/18/3027573/28124/en/Top-Growth-Opportunities-in-the-Medical-Device-Industry-for-2025-Clinical-Wearables-Heart-Valves-and-Automation-Solutions-to-Drive-Transformational-Growth.html?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://bimedis.com/top/medical-consumable-supplies?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.mckinsey.com/industries/healthcare/our-insights/what-to-expect-in-us-healthcare-in-2025-and-beyond?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.alpha-sense.com/blog/trends/medical-device-trends/?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://hmedicalinc.com/2024/10/16/what-to-expect-from-the-medical-supply-industry-in-2025/?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.completemedicalsupply.net/post/top-5-most-searched-medical-supplies-of-2025?srsltid=AfmBOoo5WM1q91oi6m31dPYdIOIt9eDK6Dd2WO8nY-7Yi2t-GFDAMOj4&accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www2.deloitte.com/us/en/insights/industry/health-care/life-sciences-and-health-care-industry-outlooks/2025-global-health-care-executive-outlook.html?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://www.thebusinessresearchcompany.com/report/healthcare-consumables-global-market-report?accio_page_id=ii4vifwnocibasqb19633b90ec91fc7cbaabaf17ff&spm=a2700.accio_thread",
            "https://customcy.com/blog/craft-handmade-stats/?accio_page_id=ii4vifwnocibasqb1963883abea1bb2124bced8980&spm=a2700.accio_thread",
            "https://www.archivemarketresearch.com/reports/art-supplies-16207?accio_page_id=ii4vifwnocibasqb1963883abea1bb2124bced8980&spm=a2700.accio_thread",
            "https://www.cognitivemarketresearch.com/art-supplies-market-report?srsltid=AfmBOooLuZ7-dfte2ZNQL3Kj9xq1t8i-9aedC4ILwkdqcC-ksdptqq-o&accio_page_id=ii4vifwnocibasqb1963883abea1bb2124bced8980&spm=a2700.accio_thread",
            "https://www.artsy.net/article/artsy-editorial-5-themes-will-define-art-market-2025?accio_page_id=ii4vifwnocibasqb1963883abea1bb2124bced8980&spm=a2700.accio_thread",
            "https://www.360iresearch.com/library/intelligence/arts-crafts-supplies?accio_page_id=ii4vifwnocibasqb1963883abea1bb2124bced8980&spm=a2700.accio_thread",
            "https://www.businessresearchinsights.com/market-reports/art-supplies-market-120824",
            "https://www.pbig.ml.com/articles/art-market-spring-update.html?accio_page_id=ii4vifwnocibasqb1963883abea1bb2124bced8980&spm=a2700.accio_thread",
            "https://markwideresearch.com/art-supplies-market/?accio_page_id=ii4vifwnocibasqb1963883abea1bb2124bced8980&spm=a2700.accio_thread",
            "https://www.thebusinessresearchcompany.com/report/arts-and-crafts-global-market-report?accio_page_id=ii4vifwnocibasqb1963883abea1bb2124bced8980&spm=a2700.accio_thread",
            "https://www.globalgrowthinsights.com/market-reports/art-paint-market-108198?accio_page_id=ii4vifwnocibasqb1963883abea1bb2124bced8980&spm=a2700.accio_thread",
            "https://china.mintel.com/",
            "https://www.shopify.com/blog/tiktok-products?accio_page_id=ii4vifwnocibasqb1963883abea1bb2124bced8980&spm=a2700.accio_thread",
            "https://www.shopify.com/blog/crafts-to-make-and-sell?accio_page_id=ii4vifwnocibasqb1963883abea1bb2124bced8980&spm=a2700.accio_thread",
            "https://reports.valuates.com/market-reports/QYRE-Auto-27F11890/global-and-united-states-art-supplies?accio_page_id=ii4vifwnocibasqb1963883abea1bb2124bced8980&spm=a2700.accio_thread",
            "https://artshortlist.com/en/journal/article/major-art-market-trends-in-2025?accio_page_id=ii4vifwnocibasqb1963883abea1bb2124bced8980&spm=a2700.accio_thread",
            "https://www.cognitivemarketresearch.com/art-supplies-market-report?srsltid=AfmBOoov7j0IfzVk0iSL81Qe6WMRJiQJK_6A-248xuzhHOh2sbdqBlPO&accio_page_id=ii4vifwnocibasqb1963883abea1bb2124bced8980&spm=a2700.accio_thread",
            "https://www.ml.com/articles/art-market-spring-update.html?accio_page_id=ii4vifwnocibasqb1963883abea1bb2124bced8980&spm=a2700.accio_thread",
            "https://www.precedenceresearch.com/us-protein-supplements-market?accio_page_id=ii4vifwnocibasqb1963887af0414609d17df01257&spm=a2700.accio_thread",
            "https://www.globenewswire.com/news-release/2025/03/14/3042871/28124/en/North-America-Protein-Market-Forecast-Report-and-Company-Analysis-2025-2033-Featuring-General-Mills-Jamieson-Wellness-Kellogg-Mars-Mondelez-PepsiCo-Simply-Good-Foods-Hershey-Vorlo.html?accio_page_id=ii4vifwnocibasqb1963887af0414609d17df01257&spm=a2700.accio_thread",
            "https://www.nutraingredients.com/Article/2025/01/06/need-to-know-nutrition-regulation-changes-for-2025/?accio_page_id=ii4vifwnocibasqb1963887af0414609d17df01257&spm=a2700.accio_thread",
            "https://www.preparedfoods.com/articles/129997-the-protein-revolution-trends-shaping-consumer-choices-in-2025?accio_page_id=ii4vifwnocibasqb1963887af0414609d17df01257&spm=a2700.accio_thread",
            "https://www.fda.gov/food/guidance-regulation-food-and-dietary-supplements/guidance-documents-regulatory-information-topic-food-and-dietary-supplements?accio_page_id=ii4vifwnocibasqb1963887af0414609d17df01257&spm=a2700.accio_thread",
            "https://www.gminsights.com/industry-analysis/protein-powder-market?accio_page_id=ii4vifwnocibasqb1963887af0414609d17df01257&spm=a2700.accio_thread",
            "https://www.grandviewresearch.com/industry-analysis/protein-supplements-market?accio_page_id=ii4vifwnocibasqb1963887af0414609d17df01257&spm=a2700.accio_thread",
            "https://www.sidley.com/en/insights/newsupdates/2025/01/2025-food-and-supplements-outlook?accio_page_id=ii4vifwnocibasqb1963887af0414609d17df01257&spm=a2700.accio_thread",
            "https://vitaquest.com/exploring-the-trends-for-2025-in-the-dietary-supplement-market/?accio_page_id=ii4vifwnocibasqb1963887af0414609d17df01257&spm=a2700.accio_thread",
            "https://www.futuremarketinsights.com/reports/whey-basic-proteinp-isolates-market",
            "https://www.globenewswire.com/news-release/2024/11/25/2986859/28124/en/Protein-Supplements-Market-Global-Forecast-Report-and-Company-Analysis-2025-2033-Featuring-Abbott-Laboratories-Herbalife-Nestle-Simply-Good-Foods-WK-Kellogg-PepsiCo-and-Glanbia.html?accio_page_id=ii4vifwnocibasqb1963887af0414609d17df01257&spm=a2700.accio_thread",
            "https://www.fortunebusinessinsights.com/u-s-protein-supplements-market-107171",
            "https://www.feedstuffs.com/livestock-and-poultry-market-news/three-key-trends-in-2025-protein-consumption-and-beyond?accio_page_id=ii4vifwnocibasqb1963887af0414609d17df01257&spm=a2700.accio_thread",
            "https://www.thebusinessresearchcompany.com/report/protein-supplements-global-market-report?accio_page_id=ii4vifwnocibasqb1963887af0414609d17df01257&spm=a2700.accio_thread",
            "https://lumivisage.com/blog/best-skincare-tools/?accio_page_id=ii4vifwnocibasqb196388df4be874b5d4d15f2c3f&spm=a2700.accio_thread",
            "https://www.globenewswire.com/news-release/2025/02/21/3030347/0/en/Trends-Shaping-the-Cosmetic-Skin-Care-Industry-2025-2030-Featuring-Analysis-of-369-Major-Companies.html?accio_page_id=ii4vifwnocibasqb196388df4be874b5d4d15f2c3f&spm=a2700.accio_thread",
            "https://explodingtopics.com/blog/beauty-trends?accio_page_id=ii4vifwnocibasqb196388df4be874b5d4d15f2c3f&spm=a2700.accio_thread",
            "https://www.beautyindependent.com/skincare-trends-soaring-2025/?accio_page_id=ii4vifwnocibasqb196388df4be874b5d4d15f2c3f&spm=a2700.accio_thread",
            "https://www.forbes.com/sites/richardkestenbaum/2025/03/03/beauty-retail-in-2025-emerging-trends-shaping-the-market/?accio_page_id=ii4vifwnocibasqb196388df4be874b5d4d15f2c3f&spm=a2700.accio_thread",
            "https://clarkstonconsulting.com/insights/2025-skincare-industry-trends/?accio_page_id=ii4vifwnocibasqb196388df4be874b5d4d15f2c3f&spm=a2700.accio_thread",
            "https://www.elle.com/beauty/makeup-skin-care/g63527136/best-skin-care-tools/?accio_page_id=ii4vifwnocibasqb196388df4be874b5d4d15f2c3f&spm=a2700.accio_thread",
            "https://www.ecomundo.eu/en/blog/beauty-tech-2025?accio_page_id=ii4vifwnocibasqb196388df4be874b5d4d15f2c3f&spm=a2700.accio_thread",
            "https://www.fortunebusinessinsights.com/beauty-tools-market-106541",
            "https://www.allure.com/story/skin-care-trends-2025?accio_page_id=ii4vifwnocibasqb196388df4be874b5d4d15f2c3f&spm=a2700.accio_thread",
            "https://www.askattest.com/blog/articles/beauty-industry-trends?accio_page_id=ii4vifwnocibasqb196388df4be874b5d4d15f2c3f&spm=a2700.accio_thread",
            "https://business.yougov.com/content/51125-us-skincare-trends-report-2025?accio_page_id=ii4vifwnocibasqb196388df4be874b5d4d15f2c3f&spm=a2700.accio_thread",
            "https://www.globenewswire.com/news-release/2025/02/12/3024823/0/en/Skincare-Devices-Market-and-Competition-Analysis-2025-2030-Featuring-42-Major-Companies-Including-Alma-Lasers-Bausch-Health-Companies-Lumenis-Merz-Pharma-More.html?accio_page_id=ii4vifwnocibasqb196388df4be874b5d4d15f2c3f&spm=a2700.accio_thread",
            "https://www.greyb.com/blog/2025-beauty-devices-trends-report/?accio_page_id=ii4vifwnocibasqb196388df4be874b5d4d15f2c3f&spm=a2700.accio_thread",
            "https://orichi.info/best-items-to-sell-on-shopify/?accio_page_id=ii4vifwnocibasqb19638938870ca2c6aab8f9174d&spm=a2700.accio_thread",
            "https://www.shopify.com/retail/how-to-use-google-trends-to-start-and-run-a-retail-business?accio_page_id=ii4vifwnocibasqb19638938870ca2c6aab8f9174d&spm=a2700.accio_thread",
            "https://pos.toasttab.com/blog/on-the-line/bar-trends?srsltid=AfmBOorP95y4yoBAmSTcQ5YjKggUK-hY6pO9KjyGCPPL0rKW8sRxrPwl&accio_page_id=ii4vifwnocibasqb19638938870ca2c6aab8f9174d&spm=a2700.accio_thread",
            "https://www.shopify.com/blog/trending-products?accio_page_id=ii4vifwnocibasqb19638938870ca2c6aab8f9174d&spm=a2700.accio_thread",
            "https://www.webstaurantstore.com/blog/2370/bar-trends.html?srsltid=AfmBOoqp0egkfhAzshZfATR-a8JqmCA37iZdaV3-PhnUy8x92zJg8fUA",
            "https://homebar.ie/blog/news-trends/bar-accessories-on-the-rise-2025?accio_page_id=ii4vifwnocibasqb19638938870ca2c6aab8f9174d&spm=a2700.accio_thread",
            "https://pmarketresearch.com/product/worldwide-bar-accessories-market-research-2024-by-type-application-participants-and-countries-forecast-to-2030/?accio_page_id=ii4vifwnocibasqb19638938870ca2c6aab8f9174d&spm=a2700.accio_thread",
            "https://sandiegobeer.news/stay-ahead-10-game-changing-bar-innovations-for-2025/?accio_page_id=ii4vifwnocibasqb19638938870ca2c6aab8f9174d&spm=a2700.accio_thread",
            "https://abarabove.com/?accio_page_id=ii4vifwnocibasqb19638938870ca2c6aab8f9174d&spm=a2700.accio_thread",
            "https://www.diageobaracademy.com/en-us/home/cocktail-trends-and-festive-serves/6-bar-industry-trends-tipped-for-2025?accio_page_id=ii4vifwnocibasqb19638938870ca2c6aab8f9174d&spm=a2700.accio_thread",
            "https://www.webstaurantstore.com/blog/2370/bar-trends.html?srsltid=AfmBOopBUENjy8IAK9WLtasV-xRvcCuHM0C0VAopym0LmgWzPRoPQxMm",
            "https://indecrafts.com/blogs/news/10-best-home-bar-essentials-you-should-have-in-2025?srsltid=AfmBOooUnqNlr9-3Ykbe3Fy3_umbURPGPymngxi4L35jZlmLYEWoSutY&accio_page_id=ii4vifwnocibasqb19638938870ca2c6aab8f9174d&spm=a2700.accio_thread",
            "https://www.shopify.com/blog/tiktok-trend-discovery?accio_page_id=ii4vifwnocibasqb19638938870ca2c6aab8f9174d&spm=a2700.accio_thread",
            "https://pos.toasttab.com/blog/on-the-line/bar-industry-trends-and-statistics?srsltid=AfmBOooTmtnNE1-NOCTWKTRYMOU_dWLIv-K0Rz0iLoI3Y8N0E2pGqAeV&accio_page_id=ii4vifwnocibasqb19638938870ca2c6aab8f9174d&spm=a2700.accio_thread"
    );



    public static void main(String[] args) {
        try {String url9 = "https://www.youtube.com/watch?v=H9alPA6ApZE";

//            SiteNameExtractor service = new SiteNameExtractor();
//            // 异步执行示例
//            service.processAsync(urls)
//                    .thenAccept(result -> result.forEach(System.out::println))
//                    .join(); // 等待异步完成
//            // 关闭线程池（生产环境需确保调用）
//            service.shutdown();

            FaviconResult siteInfo = getSiteInfo(url9);

//            Site site = null;
//            System.out.println(site.siteIcon);

//            Document doc = Jsoup.parse(readFileContent("/Users/xiaoyuntao/Desktop/icon.html"));
//            Site siteInfo = getSiteInfo(doc, "https://preproduct.io/best-selling-out-of-stock-fashion-products?accio_page_id=x8ehiie4tqcasp4s19647fe912f1ba0dd8c7516d7b&spm=a2700.accio_thread");
            System.out.println(siteInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭线程池（建议在应用关闭时调用）
     */
    public void shutdown() {
        TEST_POOL.shutdown();
        try {
            if (!TEST_POOL.awaitTermination(60, TimeUnit.SECONDS)) {
                TEST_POOL.shutdownNow();
            }
        } catch (InterruptedException e) {
            TEST_POOL.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

//    public CompletableFuture<List<Site>> processAsync(List<String> urls) {
//        return CompletableFuture.supplyAsync(
//                () -> urls.parallelStream()
//                        .map(SiteNameExtractor::getSiteInfo)
//                        .collect(Collectors.toList()),
//                TEST_POOL
//        );
//    }

//    public static Site getSiteInfo(Document doc, String url) {
//        try {
//            // 获取网站名称
//            String siteName = extractWebsiteName(doc, url);
//
//            // 获取网站图标
////            String siteIcon = getSiteIcon(doc, url);
//            FaviconResult faviconUrl = getFaviconUrl(url, doc);
//            System.out.println(faviconUrl);
//            System.out.println("fileExtension: " + getExtension(faviconUrl.faviconUrl) + " url: " + faviconUrl.getFaviconUrl());
//            // 返回包含网站名称和图标的对象
//            return new Site(siteName, siteIcon);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new Site("", "");
//        }
//    }



    public static FaviconResult getSiteInfo(String url) {
        try {
            // 使用 Jsoup 连接到网页
            Document doc = Jsoup.connect(url).get();
            return getFaviconUrl(url, doc);
        } catch (Exception e) {
            e.printStackTrace();
            return new FaviconResult(false, "");
        }
    }
    public static String extractWebsiteName(Document doc, String url) {
        Element element = doc.selectFirst("meta[property=og:site_name]");
        if (element == null) {
            return extractWebsiteName(url);
        }
        return element.attr("content");
    }

    /**
     * @param url 网站url
     * @return 只是简单的获取,推荐从网站的meta信息里面获取
     */
    public static String extractWebsiteName(String url) {
        String host = getHost(url);
        System.out.println("host: " + host);
        if (null == host) {
            return null;
        }
        //先处理掉www
        host = host.replaceAll("www.", "");
        String[] split = host.split("\\.");
        if (split.length >= 2) {
            //去掉域名的最后一位
            String[] newArray = Arrays.copyOf(split, split.length - 1);
            if (newArray.length == 1) {
                return capitalize(newArray[0].replace(".com", ""));
            }
            return capitalize(String.join(".", newArray).replace(".com", ""));
        }
        return capitalize(split[0]);
    }

    /**
     * 首字母大写
     * @param str
     * @return
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static @Nullable URL createUrl(String urlString) {
        try {
            if (!urlString.startsWith("http")) {
                urlString = "http://" + urlString;
            }
            URL url = new URL(urlString);
            if (StringUtils.isEmpty(url.getHost())) {
                return null;
            }
            return url;
        } catch (Throwable ignore) {
//            LOGGER.error("createUrl urlString={}",urlString, ignore);
        }
        return null;
    }

    public static String getHost(String urlString) {
        URL url = createUrl(urlString);
        if (null == url) {
            return null;
        }
        return url.getHost();
    }

    private static String getSiteName(Document doc) {
        Element element = doc.selectFirst("meta[property=og:site_name]");
        if (element == null) {
            return  "";
        }
        return element.attr("content");
    }

    private static String getSiteIcon(Document doc, String url) {
        // 尝试获取 <link rel="icon">
        Element iconLink = doc.selectFirst("link[rel=icon]");
        if (iconLink != null) {
            String iconHref = iconLink.attr("href");
            System.out.println("get icon");
            return assembleFullUrl(url, iconHref);
        }

        // 尝试获取 <link rel="shortcut icon">
        Element shortcutIcon = doc.selectFirst("link[rel=shortcut icon]");
        if (shortcutIcon != null) {
            String shortcutHref = shortcutIcon.attr("href");
            System.out.println("get shortcut icon");
            return assembleFullUrl(url, shortcutHref);
        }

        // 默认返回根路径的 favicon.ico
        return assembleFullUrl(url, "/favicon.ico");
    }

    /**
     * 将相对路径的资源 URL 拼接成完整的 URL。
     * @param baseUrl 原始 URL 的域名部分
     * @param resourcePath 资源的相对路径或绝对路径
     * @return 拼接后的完整 URL
     */
    private static String assembleFullUrl(String baseUrl, String resourcePath) {
        try {
            // 如果 resourcePath 已经是一个完整的 URL，直接返回
            if (resourcePath.startsWith("http://") || resourcePath.startsWith("https://")) {
                return resourcePath;
            }
            // 将 baseUrl 和 resourcePath 转换为 URI 并解析
            URI baseUri = new URI(baseUrl);
            URI fullUri = baseUri.resolve(resourcePath);
            return fullUri.toString();
        } catch (URISyntaxException e) {
            // 如果解析失败，返回原始路径作为回退方案
            System.err.println("Invalid URL or Resource Path: " + e.getMessage());
            return resourcePath;
        }
    }

    public static record Site(String siteName, String siteIcon) {
    }

    public static FaviconResult getFaviconUrl(String url, Document doc) {

        try {
            // 标准化URL处理
            URL parsedUrl;

            try {
                parsedUrl = new URL(url);
            } catch (MalformedURLException e) {
                parsedUrl = new URL("http://" + url);  // 默认添加http
            }

            String baseUrl = parsedUrl.getProtocol() + "://" + parsedUrl.getHost() + (parsedUrl.getPort() != -1 ? ":" + parsedUrl.getPort() : ""); // 保留完整结构（含端口等）

            // 改进的图标匹配逻辑
            Elements iconLinks = doc.select("link[rel~=(?i)icon]"); // 匹配所有包含icon的link标签

            // 优先级排序匹配
            for (String relOrder : new String[]{"shortcut icon", "icon"}) {
                for (Element link : iconLinks) {
                    if (link.attr("rel").toLowerCase().contains(relOrder)) {
                        String faviconUrl = link.attr("href");
                        return new FaviconResult(true, resolveUrl(baseUrl, faviconUrl));
                    }
                }
            }

            // 标准回退路径（保留端口信息）
            return new FaviconResult(false, baseUrl + "/favicon.ico");

        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            return new FaviconResult(false, "");
        }
    }

    @Getter
    public static class FaviconResult {
        /**
         * 是否匹配到 "shortcut icon" 或 "icon"
         */
        private final boolean isMatched;

        private final String faviconUrl;

        public FaviconResult(boolean isMatched, String faviconUrl) {
            this.isMatched = isMatched;
            this.faviconUrl = faviconUrl;
        }

        @Override
        public String toString() {
            return "FaviconResult{" +
                    "isMatched=" + isMatched +
                    ", faviconUrl='" + faviconUrl + '\'' +
                    '}';
        }
    }

    private static String resolveUrl(String baseUrl, String faviconUrl) {
        try {
            URL url = new URL(new URL(baseUrl), faviconUrl);
            return url.toURI().normalize().toString();
        } catch (Throwable e) {
            // 返回原始的faviconUrl， 如果解析失败，则根据需要处理
            return faviconUrl;
        }
    }

    public static String readFileContent(String filePath) throws IOException {
        // 将路径字符串转换为 Path 对象
        Path path = Paths.get(filePath);

        // 读取文件所有内容并返回为字符串
        return Files.readString(path);
    }

    /**
     * 提取资源 URL 的扩展名（不包括点号）
     *
     * @param resourceUrl 资源的 URL 字符串
     * @return URL 的扩展名（不包括点号），如果没有扩展名则返回空字符串
     */
    public static String getExtension(String resourceUrl) {
        // 检查输入是否为空或无效
        if (resourceUrl == null || resourceUrl.isEmpty()) {
            return "";
        }

        // 查找最后一个 '.' 和 '/' 的索引位置
        int lastDotIndex = resourceUrl.lastIndexOf('.');
        int lastSlashIndex = resourceUrl.lastIndexOf('/');

        // 确保 '.' 在 '/' 之后且存在
        if (lastDotIndex > lastSlashIndex && lastDotIndex != -1) {
            // 返回从最后一个 '.' 后面的字符串（不包括点号）
            return resourceUrl.substring(lastDotIndex + 1);
        }

        // 如果没有扩展名，返回空字符串
        return "";
    }
}
