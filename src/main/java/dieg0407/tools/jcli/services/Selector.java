package dieg0407.tools.jcli.services;

import dieg0407.tools.jcli.services.dependencies.models.Dependency;
import java.util.List;
import java.util.Scanner;

public interface Selector<T> {

  T choose(List<T> options);

  class DependencySelector implements Selector<Dependency> {
    @Override
    public Dependency choose(List<Dependency> options) {
      for(int i = 0; i < options.size(); i++) {
        System.out.println((i+1) + " --> " + options.get(i));
      }
      System.out.println("Choose a number: ");
      try(Scanner scanner = new Scanner(System.in)) {
        int choice = scanner.nextInt();
        return options.get(choice - 1);
      } catch (Exception e) {
        System.out.println("Invalid input");
        return choose(options);
      }
    }
  }
}
