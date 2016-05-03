import org.sql2o.*; // for DB support
import org.junit.*; // for @Before and @After

import org.fluentlenium.adapter.FluentTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.*;

public class AppTest extends FluentTest {
  public WebDriver webDriver = new HtmlUnitDriver();

  @Override
  public WebDriver getDefaultDriver() {
    return webDriver;
  }

  @ClassRule
  public static ServerRule server = new ServerRule();

  @Before
  public void setUp() {
    DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/to_do_test", null, null);
  }

  @After
  public void tearDown() {
    try(Connection con = DB.sql2o.open()) {
      String deleteTasksQuery = "DELETE FROM tasks *;";
      String deleteCategoriesQuery = "DELETE FROM categories *;";
      con.createQuery(deleteTasksQuery).executeUpdate();
      con.createQuery(deleteCategoriesQuery).executeUpdate();
    }
  }

    @Test
  public void categoryIsCreatedTest() {
    goTo("http://localhost:4567/");
    click("a", withText("Add a new category"));
    fill("#name").with("Household chores");
    submit(".btn");
    assertThat(pageSource()).contains("Your category has been saved.");
  }

  @Test
  public void categoryIsDisplayedTest() {
    Category myCategory = new Category("Household chores");
    myCategory.save();
    String categoryPath = String.format("http://localhost:4567/categories/%d", myCategory.getId());
    goTo(categoryPath);
    assertThat(pageSource()).contains("Household chores");
  }

   @Test
  public void allTasksDisplayDescriptionOnCategoryPage() {
    Category myCategory = new Category("Household chores");
    myCategory.save();
    Task firstTask = new Task("Mow the lawn", myCategory.getId());
    firstTask.save();
    Task secondTask = new Task("Do the dishes", myCategory.getId());
    secondTask.save();
    String categoryPath = String.format("http://localhost:4567/categories/%d", myCategory.getId());
    goTo(categoryPath);
    assertThat(pageSource()).contains("Mow the lawn");
    assertThat(pageSource()).contains("Do the dishes");
  }


}
