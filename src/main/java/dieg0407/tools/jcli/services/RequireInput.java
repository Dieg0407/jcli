package dieg0407.tools.jcli.services;

public interface RequireInput {

  <T> T choose(Iterable<T> options);

}
