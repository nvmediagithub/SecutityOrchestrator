import 'package:flutter/material.dart';

import 'bpmn_viewer_stub.dart'
    if (dart.library.html) 'bpmn_viewer_web.dart'
    as bpmn_platform;

class BpmnViewer extends StatelessWidget {
  final String bpmnXml;
  final double height;

  const BpmnViewer({super.key, required this.bpmnXml, this.height = 420});

  @override
  Widget build(BuildContext context) {
    return bpmn_platform.buildBpmnView(bpmnXml: bpmnXml, height: height);
  }
}
