package dieg0407.tools.jcli.validators;

import java.util.Set;

public final class GroupIdValidator {
  private static final Set<String> RESERVED_KEYWORDS = Set.of(
      "abstract", "assert", "boolean", "break", "byte", "case", "catch",
      "char", "class", "const", "continue", "default", "do", "double",
      "else", "enum", "exports", "extends", "final", "finally", "float",
      "for", "goto", "if", "implements", "import", "instanceof", "int",
      "interface", "long", "module", "native", "new", "package", "private",
      "protected", "public", "requires", "return", "short", "static",
      "strictfp", "super", "switch", "synchronized", "this", "throw",
      "throws", "transient", "try", "var", "void", "volatile", "while"
  );
  public static boolean isValid(final String groupId) {
    final var parts = groupId.split("\\.");
    for (final var part : parts) {
      if (RESERVED_KEYWORDS.contains(part) || !part.matches("^[a-zA-Z_$][a-zA-Z\\d_$]*$")) {
          return false;
      }
    }
    return true;
  }
}
