class InputFieldSpec {
  final String name;
  final String label;
  final String? description;
  final bool required;

  const InputFieldSpec({
    required this.name,
    required this.label,
    this.description,
    required this.required,
  });

  factory InputFieldSpec.fromMap(Map entry) {
    final name = entry['name']?.toString() ?? '';
    return InputFieldSpec(
      name: name,
      label: entry['label']?.toString() ?? humanize(name),
      description: entry['description']?.toString(),
      required: entry['required'] is bool ? entry['required'] as bool : true,
    );
  }

  static InputFieldSpec? fromNullable(Object? value) {
    if (value is Map) {
      final name = value['name']?.toString();
      if (name == null || name.isEmpty) return null;
      return InputFieldSpec.fromMap(value);
    }
    return null;
  }
}

List<InputFieldSpec> parseInputFields(dynamic raw) {
  if (raw is List) {
    return raw
        .map(InputFieldSpec.fromNullable)
        .whereType<InputFieldSpec>()
        .toList();
  }
  return const <InputFieldSpec>[];
}

String humanize(String value) {
  final normalized = value.replaceAll('_', ' ').replaceAll('-', ' ');
  if (normalized.isEmpty) return 'Input';
  return normalized[0].toUpperCase() + normalized.substring(1);
}
