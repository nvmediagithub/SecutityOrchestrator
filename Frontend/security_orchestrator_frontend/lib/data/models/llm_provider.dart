/// LLM Provider enum matching SecurityOrchestrator backend LLMProvider
enum LLMProvider {
  local('LOCAL'),
  openrouter('OPENROUTER');

  const LLMProvider(this.value);
  final String value;

  static LLMProvider fromString(String value) {
    return LLMProvider.values.firstWhere(
      (provider) => provider.value == value,
      orElse: () => throw ArgumentError('Invalid LLMProvider: $value'),
    );
  }
}