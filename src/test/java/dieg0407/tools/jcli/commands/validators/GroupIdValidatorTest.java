package dieg0407.tools.jcli.commands.validators;

import static org.assertj.core.api.Assertions.assertThat;

import dieg0407.tools.jcli.TestTypes;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Tag(TestTypes.UNIT)
public class GroupIdValidatorTest {

  @ParameterizedTest
  @ValueSource(strings = {
      "hyphenated-name.example.org",
      "example.int",
      "123name.example.com",
      "com.abstract", // 'abstract'
      "com.assert", // 'assert'
      "com.boolean", // 'boolean'
      "com.break", // 'break'
      "com.byte", // 'byte'
      "com.case", // 'case'
      "com.catch", // 'catch'
      "com.char", // 'char'
      "com.class", // 'class'
      "com.const", // 'const' (not used, but reserved)
      "com.continue", // 'continue'
      "com.default", // 'default'
      "com.do", // 'do'
      "com.double", // 'double'
      "com.else", // 'else'
      "com.enum", // 'enum'
      "com.exports", // 'exports'
      "com.extends", // 'extends'
      "com.final", // 'final'
      "com.finally", // 'finally'
      "com.float", // 'float'
      "com.for", // 'for'
      "com.goto", // 'goto' (not used, but reserved)
      "com.if", // 'if'
      "com.implements", // 'implements'
      "com.import", // 'import'
      "com.instanceof", // 'instanceof'
      "com.int", // 'int'
      "com.interface", // 'interface'
      "com.long", // 'long'
      "com.module", // 'module'
      "com.native", // 'native'
      "com.new", // 'new'
      "com.package", // 'package'
      "com.private", // 'private'
      "com.protected", // 'protected'
      "com.public", // 'public'
      "com.requires", // 'requires'
      "com.return", // 'return'
      "com.short", // 'short'
      "com.static", // 'static'
      "com.strictfp", // 'strictfp'
      "com.super", // 'super'
      "com.switch", // 'switch'
      "com.synchronized", // 'synchronized'
      "com.this", // 'this'
      "com.throw", // 'throw'
      "com.throws", // 'throws'
      "com.transient", // 'transient'
      "com.try", // 'try'
      "com.var", // 'var'
      "com.void", // 'void'
      "com.volatile", // 'volatile'
      "com.while", // 'while'
      ""
  })
  void shouldNotBeValid(String groupId) {
    final var isValid = GroupIdValidator.isValid(groupId);
    assertThat(isValid).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "org.example.hyphenated_name",
      "int_.example",
      "com.example._123name"
  })
  void shouldBeValid(String groupId) {
    final var isValid = GroupIdValidator.isValid(groupId);
    assertThat(isValid).isTrue();
  }
}
