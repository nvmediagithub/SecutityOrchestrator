import 'package:equatable/equatable.dart';

class AnalysisRequest extends Equatable {
  final String documentContent;
  final String documentType;
  final String? documentName;
  final Map<String, dynamic>? metadata;
  final List<String>? analysisTypes;

  const AnalysisRequest({
    required this.documentContent,
    required this.documentType,
    this.documentName,
    this.metadata,
    this.analysisTypes,
  });

  Map<String, dynamic> toJson() {
    return {
      'documentContent': documentContent,
      'documentType': documentType,
      'documentName': documentName,
      'metadata': metadata,
      'analysisTypes': analysisTypes,
    };
  }

  @override
  List<Object?> get props => [
    documentContent,
    documentType,
    documentName,
    metadata,
    analysisTypes,
  ];
}