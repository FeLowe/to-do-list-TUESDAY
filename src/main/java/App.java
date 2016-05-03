import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;

public class App {
  public static void main(String[] args) {

    ProcessBuilder process = new ProcessBuilder();
       Integer port;
       if (process.environment().get("PORT") != null) {
           port = Integer.parseInt(process.environment().get("PORT"));
       } else {
           port = 4567;
       }

      setPort(port);


    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    get("/", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/index.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    // display form
    get("/categories/new", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      model.put("template", "templates/category-form.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    // process form
    post("/categories", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      String inputtedString = request.queryParams("name");
      Category userInput = new Category(inputtedString);
      userInput.save();
      // CREATE CATEGORY OBJECT HERE
      model.put("category", userInput);
      model.put("template", "templates/category-success.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());


    get("/categories", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();

      model.put("categories", Category.all());
      model.put("template", "templates/categories.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/categories/:id", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      Category categoryInput = Category.find(Integer.parseInt(request.params(":id")));
      model.put("category", categoryInput);
      model.put("template", "templates/category.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("categories/:id/tasks/new", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      Category category = Category.find(Integer.parseInt(request.params(":id")));
      model.put("category", category);
      model.put("template", "templates/task-category-form.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/tasks", (request,response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();

      Category category = Category.find(Integer.parseInt(request.queryParams("categoryId")));

      String description = request.queryParams("description");

      Task newTask = new Task(description, category.getId());

      newTask.save();

      model.put("category", category);
      model.put("template", "templates/category-tasks-success.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/allTasks", (request,response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      List<Task> allTasks = Task.all();

      model.put("tasks", allTasks);
      model.put("template", "templates/alltasks.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get ("/task/:id", (request, response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      Task task = Task.find(Integer.parseInt(request.params(":id")));
      model.put("task", task);
      model.put("template", "templates/task.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post ("/delete", (request,response) -> {
      HashMap<String, Object> model = new HashMap<String, Object>();
      int deleteTask = Integer.parseInt(request.queryParams("deleteTask"));
      Task.deleteTask(deleteTask);
      model.put("template", "templates/deletetask.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

  }
}
