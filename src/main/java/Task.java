import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;

public class Task {

    private int id;
    private String description;
    private int categories_id;

    public Task(String description, int categoryId) {
      this.description = description;
      this.categories_id = categoryId;
    }

    public String getDescription() {
      return description;
    }

    public int getId() {
      return id;
    }
    public int getCategoryId() {
    return categories_id;
  }

    public static List<Task> all() {
       String sql = "SELECT id, description, categories_id FROM tasks";
      try(Connection con = DB.sql2o.open()) {
        return con.createQuery(sql).executeAndFetch(Task.class);
      }
    }

    public void save() {
      try(Connection con = DB.sql2o.open()) {
         String sql = "INSERT INTO tasks(description, categories_id) VALUES (:description, :categories_id)";
        this.id = (int) con.createQuery(sql, true)
          .addParameter("description", this.description)
          .addParameter("categories_id", this.categories_id)
          .executeUpdate()
          .getKey();
      }
    }

    @Override
    public boolean equals(Object otherTask){
      if (!(otherTask instanceof Task)) {
        return false;
      } else {
        Task newTask = (Task) otherTask;
        return this.getDescription().equals(newTask.getDescription()) &&
               this.getId() == newTask.getId() &&
               this.getCategoryId() == newTask.getCategoryId();
      }
    }

    public static Task find(int id) {
      try(Connection con = DB.sql2o.open()) {
        String sql = "SELECT * FROM tasks where id=:id";
        Task task = con.createQuery(sql)
          .addParameter("id", id)
          .executeAndFetchFirst(Task.class);
        return task;
      }
    }

    public static void deleteTask(int id) {
      try(Connection con = DB.sql2o.open()) {
        String sql = "DELETE FROM tasks WHERE id = :id";
        con.createQuery(sql).addParameter("id", id).executeUpdate();
      }
    }

  }
