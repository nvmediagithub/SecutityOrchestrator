import 'package:flutter/material.dart';

Widget buildBpmnView({required String bpmnXml, required double height}) {
  return Container(
    width: double.infinity,
    padding: const EdgeInsets.all(16),
    color: Colors.black12,
    child: SizedBox(
      height: height,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Предпросмотр BPMN недоступен на данной платформе. '
            'Отображается текстовое содержимое диаграммы.',
            style: TextStyle(fontSize: 12, color: Colors.black54),
          ),
          const SizedBox(height: 8),
          Expanded(
            child: SingleChildScrollView(
              child: SelectableText(
                bpmnXml,
                style: const TextStyle(fontFamily: 'monospace'),
              ),
            ),
          ),
        ],
      ),
    ),
  );
}
