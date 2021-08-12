package cspr.fitnessapp.server.entity;

public class Greeting {
  private static final String TEMPLATE = "Hello, %s!";

  private final long id;
  private String content;

  public Greeting(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String name) {
    this.content = String.format(TEMPLATE, name);
  }

}
