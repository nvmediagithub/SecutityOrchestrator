import 'dart:html' as html;
import 'dart:math';
import 'dart:ui_web' as ui_web;

import 'package:flutter/material.dart';

Widget buildBpmnView({required String bpmnXml, required double height}) {
  return _BpmnWebRenderer(bpmnXml: bpmnXml, height: height);
}

class _BpmnWebRenderer extends StatefulWidget {
  final String bpmnXml;
  final double height;

  const _BpmnWebRenderer({required this.bpmnXml, required this.height});

  @override
  State<_BpmnWebRenderer> createState() => _BpmnWebRendererState();
}

class _BpmnWebRendererState extends State<_BpmnWebRenderer> {
  late final String _viewType;

  @override
  void initState() {
    super.initState();
    final randomId = Random().nextInt(1 << 31);
    _viewType = 'bpmn-viewer-$randomId';
    _registerHtmlView(widget.bpmnXml);
  }

  @override
  void didUpdateWidget(covariant _BpmnWebRenderer oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.bpmnXml != widget.bpmnXml) {
      _registerHtmlView(widget.bpmnXml);
    }
  }

  void _registerHtmlView(String xml) {
    final iframe = html.IFrameElement()
      ..style.border = 'none'
      ..style.width = '100%'
      ..style.height = '100%'
      ..srcdoc = _buildHtml(xml);

    // ignore: undefined_prefixed_name
    ui_web.platformViewRegistry.registerViewFactory(
      _viewType,
      (int _) => iframe,
    );
  }

  String _buildHtml(String xml) {
    final escapedXml = xml
        .replaceAll(r'\', r'\\')
        .replaceAll('`', r'\`')
        .replaceAll('\$', r'\$');
    return '''
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <script src="https://unpkg.com/bpmn-js@11.5.0/dist/bpmn-viewer.production.min.js"></script>
    <style>
      html, body, #canvas { height: 100%; margin: 0; padding: 0; }
    </style>
  </head>
  <body>
    <div id="canvas"></div>
    <script>
      const viewer = new BpmnJS({container: '#canvas'});
      const xml = `$escapedXml`;
      viewer.importXML(xml).catch(err => console.error(err));
    </script>
  </body>
</html>
''';
  }

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: widget.height,
      child: HtmlElementView(viewType: _viewType),
    );
  }
}
