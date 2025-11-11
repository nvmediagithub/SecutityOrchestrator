#!/usr/bin/env python3
"""
🔍 ENHANCED BPMN API EXTRACTOR - СИСТЕМА ПАРСИНГА BPMN ДЛЯ ИЗВЛЕЧЕНИЯ БИЗНЕС-ПРОЦЕССОВ

Расширенная система для глубокого парсинга BPMN файлов и извлечения бизнес-процессов
для тестирования API с интеграцией OWASP security testing methodology.

Ключевые возможности:
- Глубокий анализ BPMN структуры (task'и, sequence flows, gateways, events)
- Извлечение API endpoints и их маппинг с OpenAPI спецификацией
- Анализ бизнес-логики и security-critical точек
- Генерация test cases на основе business workflows
- OWASP security testing integration
"""

import re
import json
import xml.etree.ElementTree as ET
from pathlib import Path
from typing import List, Dict, Optional, Tuple, Set
from dataclasses import dataclass, asdict
from datetime import datetime
from enum import Enum
import hashlib

class TaskType(Enum):
    """Типы задач в BPMN процессах"""
    USER_TASK = "userTask"
    SERVICE_TASK = "serviceTask"
    BUSINESS_RULE_TASK = "businessRuleTask"
    SCRIPT_TASK = "scriptTask"
    SEND_TASK = "sendTask"
    RECEIVE_TASK = "receiveTask"
    CALL_ACTIVITY = "callActivity"
    SUB_PROCESS = "subProcess"

class GatewayType(Enum):
    """Типы шлюзов в BPMN"""
    EXCLUSIVE = "exclusiveGateway"
    PARALLEL = "parallelGateway"
    INCLUSIVE = "inclusiveGateway"
    EVENT_BASED = "eventBasedGateway"

class EventType(Enum):
    """Типы событий в BPMN"""
    START_EVENT = "startEvent"
    END_EVENT = "endEvent"
    INTERMEDIATE_THROW_EVENT = "intermediateThrowEvent"
    INTERMEDIATE_CATCH_EVENT = "intermediateCatchEvent"
    BOUNDARY_EVENT = "boundaryEvent"

class SecurityRiskLevel(Enum):
    """Уровни security рисков"""
    LOW = "low"
    MEDIUM = "medium"
    HIGH = "high"
    CRITICAL = "critical"

@dataclass
class BPMNGateway:
    """Структура для хранения информации о шлюзе BPMN"""
    gateway_id: str
    name: str
    gateway_type: GatewayType
    position: Dict[str, int]
    incoming_flows: List[str]
    outgoing_flows: List[str]

@dataclass
class BPMNEvent:
    """Структура для хранения информации о событии BPMN"""
    event_id: str
    name: str
    event_type: EventType
    position: Dict[str, int]
    incoming_flows: List[str]
    outgoing_flows: List[str]
    event_definitions: List[str] = None

@dataclass
class BPMNDataObject:
    """Структура для хранения информации о data объекте"""
    data_object_id: str
    name: str
    data_object_ref: str
    is_collection: bool = False
    position: Dict[str, int] = None

@dataclass
class BPMNMessageFlow:
    """Структура для хранения информации о message flow"""
    message_flow_id: str
    source_ref: str
    target_ref: str
    name: str = ""
    message_ref: str = ""

@dataclass
class SecurityAssessmentPoint:
    """Точка для security assessment"""
    point_id: str
    process_step: str
    api_endpoint: str
    risk_level: SecurityRiskLevel
    owasp_category: str
    description: str
    test_vectors: List[str]
    mitigation_suggestions: List[str]

@dataclass
class BusinessLogicAnalysis:
    """Анализ бизнес-логики"""
    process_id: str
    workflow_pattern: str
    critical_operations: List[str]
    authentication_flows: List[str]
    authorization_checkpoints: List[str]
    data_validation_points: List[str]
    error_handling_scenarios: List[str]
    performance_bottlenecks: List[str]

@dataclass
class TestCase:
    """Test case для API testing"""
    test_id: str
    test_name: str
    test_type: str  # happy_path, negative, security, performance
    api_endpoint: str
    http_method: str
    test_data: Dict
    expected_response: Dict
    security_vectors: List[str] = None
    business_scenario: str = ""

@dataclass
class EnhancedAPIEndpoint:
    """Расширенная структура API endpoint с security анализом"""
    method: str
    path: str
    task_id: str
    task_name: str
    process_id: str
    sequence: int
    task_type: TaskType
    description: str = ""
    parameters: List[str] = None
    requires_auth: bool = False
    security_risk_level: SecurityRiskLevel = SecurityRiskLevel.LOW
    owasp_categories: List[str] = None
    business_criticality: str = "medium"
    test_cases: List[TestCase] = None
    data_sensitivity: str = "low"  # low, medium, high, critical
    compliance_requirements: List[str] = None

class EnhancedBPMNParser:
    """Улучшенный парсер BPMN с глубоким анализом структуры"""
    
    def __init__(self):
        self.bpmn_namespace = {
            'bpmn': 'http://www.omg.org/spec/BPMN/20100524/MODEL',
            'bpmndi': 'http://www.omg.org/spec/BPMN/20100524/DI',
            'dc': 'http://www.omg.org/spec/DD/20100524/DC',
            'di': 'http://www.omg.org/spec/DD/20100524/DI'
        }
        
        # Паттерны для извлечения API вызовов
        self.api_patterns = {
            'GET': r'GET\s+([/\w\-\{\}\.]+)',
            'POST': r'POST\s+([/\w\-\{\}\.]+)', 
            'PUT': r'PUT\s+([/\w\-\{\}\.]+)',
            'DELETE': r'DELETE\s+([/\w\-\{\}\.]+)',
            'PATCH': r'PATCH\s+([/\w\-\{\}\.]+)'
        }
        
        # Ключевые слова для security analysis
        self.security_keywords = {
            'authentication': ['auth', 'token', 'login', 'password', 'credential'],
            'authorization': ['permission', 'role', 'access', 'authorize'],
            'data_protection': ['encrypt', 'hash', 'secure', 'private'],
            'payment': ['payment', 'transfer', 'money', 'amount', 'balance'],
            'personal_data': ['personal', 'customer', 'client', 'user'],
            'financial': ['account', 'card', 'credit', 'debit', 'loan']
        }
        
        # High-risk business operations
        self.high_risk_operations = [
            'payment', 'transfer', 'balance', 'account', 'card',
            'credit', 'debit', 'loan', 'personal_data', 'auth'
        ]
    
    def parse_bpmn_structure(self, file_path: str) -> Dict:
        """Глубокий парсинг структуры BPMN файла"""
        try:
            tree = ET.parse(file_path)
            root = tree.getroot()
            
            # Извлекаем все элементы процесса
            process_elem = root.find('.//bpmn:process', self.bpmn_namespace)
            if process_elem is None:
                raise ValueError(f"Process element not found in {file_path}")
            
            process_id = process_elem.get('id', '')
            
            # Анализируем все компоненты
            structure = {
                'process_id': process_id,
                'process_name': process_elem.get('name', ''),
                'tasks': self._extract_tasks(process_elem),
                'gateways': self._extract_gateways(process_elem),
                'events': self._extract_events(process_elem),
                'sequence_flows': self._extract_sequence_flows(process_elem),
                'data_objects': self._extract_data_objects(process_elem),
                'message_flows': self._extract_message_flows(root),
                ' swim_lanes': self._extract_swim_lanes(process_elem)
            }
            
            return structure
            
        except Exception as e:
            print(f"❌ Error parsing BPMN structure: {e}")
            return {}
    
    def _extract_tasks(self, process_elem: ET.Element) -> List[Dict]:
        """Извлечение всех типов задач"""
        tasks = []
        
        # Все типы задач - включаем базовый 'task' тип
        task_types = ['task', 'userTask', 'serviceTask', 'businessRuleTask',
                     'scriptTask', 'sendTask', 'receiveTask',
                     'callActivity', 'subProcess']
        
        for task_type in task_types:
            # Пробуем разные способы поиска элементов
            elements = []
            try:
                # Сначала пробуем с namespace
                elements = process_elem.findall(f'.//bpmn:{task_type}', self.bpmn_namespace)
            except:
                # Если не получилось, пробуем без namespace
                elements = process_elem.findall(f'.//{task_type}')
            
            # Если все еще ничего не нашли, ищем все элементы и фильтруем
            if not elements and task_type == 'task':
                all_elements = process_elem.findall('.//*')
                for elem in all_elements:
                    if elem.tag.endswith('task'):
                        elements.append(elem)
            
            for elem in elements:
                task_info = {
                    'task_id': elem.get('id', ''),
                    'task_name': elem.get('name', ''),
                    'task_type': task_type,
                    'position': self._get_element_position(elem),
                    'incoming_flows': [],
                    'outgoing_flows': []
                }
                
                # Извлекаем incoming и outgoing flows более надежно
                try:
                    # Ищем incoming
                    incoming_elems = elem.findall('.//incoming')
                    if not incoming_elems:
                        incoming_elems = elem.findall('.//bpmn:incoming', self.bpmn_namespace)
                    task_info['incoming_flows'] = [flow.text or '' for flow in incoming_elems]
                except:
                    pass
                
                try:
                    # Ищем outgoing
                    outgoing_elems = elem.findall('.//outgoing')
                    if not outgoing_elems:
                        outgoing_elems = elem.findall('.//bpmn:outgoing', self.bpmn_namespace)
                    task_info['outgoing_flows'] = [flow.text or '' for flow in outgoing_elems]
                except:
                    pass
                
                # Добавляем только если есть ID и имя
                if task_info['task_id'] and task_info['task_name']:
                    tasks.append(task_info)
        
        return tasks
    
    def _extract_gateways(self, process_elem: ET.Element) -> List[BPMNGateway]:
        """Извлечение шлюзов"""
        gateways = []
        gateway_types = ['exclusiveGateway', 'parallelGateway', 
                        'inclusiveGateway', 'eventBasedGateway']
        
        for gw_type in gateway_types:
            elements = process_elem.findall(f'.//bpmn:{gw_type}', self.bpmn_namespace)
            for elem in elements:
                gateway = BPMNGateway(
                    gateway_id=elem.get('id', ''),
                    name=elem.get('name', ''),
                    gateway_type=GatewayType(gw_type),
                    position=self._get_element_position(elem),
                    incoming_flows=[flow.get('sourceRef') for flow in elem.findall('.//bpmn:incoming', self.bpmn_namespace)],
                    outgoing_flows=[flow.get('sourceRef') for flow in elem.findall('.//bpmn:outgoing', self.bpmn_namespace)]
                )
                gateways.append(gateway)
        
        return gateways
    
    def _extract_events(self, process_elem: ET.Element) -> List[BPMNEvent]:
        """Извлечение событий"""
        events = []
        event_types = ['startEvent', 'endEvent', 'intermediateThrowEvent', 
                      'intermediateCatchEvent', 'boundaryEvent']
        
        for event_type in event_types:
            elements = process_elem.findall(f'.//bpmn:{event_type}', self.bpmn_namespace)
            for elem in elements:
                event = BPMNEvent(
                    event_id=elem.get('id', ''),
                    name=elem.get('name', ''),
                    event_type=EventType(event_type),
                    position=self._get_element_position(elem),
                    incoming_flows=[flow.get('sourceRef') for flow in elem.findall('.//bpmn:incoming', self.bpmn_namespace)],
                    outgoing_flows=[flow.get('sourceRef') for flow in elem.findall('.//bpmn:outgoing', self.bpmn_namespace)]
                )
                events.append(event)
        
        return events
    
    def _extract_sequence_flows(self, process_elem: ET.Element) -> List[Dict]:
        """Извлечение sequence flows"""
        flows = []
        flow_elements = process_elem.findall('.//bpmn:sequenceFlow', self.bpmn_namespace)
        
        for flow in flow_elements:
            flow_info = {
                'flow_id': flow.get('id', ''),
                'name': flow.get('name', ''),
                'source_ref': flow.get('sourceRef', ''),
                'target_ref': flow.get('targetRef', ''),
                'condition_expression': self._get_condition_expression(flow)
            }
            flows.append(flow_info)
        
        return flows
    
    def _extract_data_objects(self, process_elem: ET.Element) -> List[BPMNDataObject]:
        """Извлечение data объектов"""
        data_objects = []
        data_elem_elements = process_elem.findall('.//bpmn:dataObject', self.bpmn_namespace)
        data_obj_ref_elements = process_elem.findall('.//bpmn:dataObjectReference', self.bpmn_namespace)
        
        # Data Objects
        for elem in data_elem_elements:
            data_obj = BPMNDataObject(
                data_object_id=elem.get('id', ''),
                name=elem.get('name', ''),
                data_object_ref=elem.get('id', ''),
                is_collection=elem.get('isCollection', 'false').lower() == 'true',
                position=self._get_element_position(elem)
            )
            data_objects.append(data_obj)
        
        # Data Object References
        for elem in data_obj_ref_elements:
            data_obj_ref = BPMNDataObject(
                data_object_id=elem.get('id', ''),
                name=elem.get('name', ''),
                data_object_ref=elem.get('dataObjectRef', ''),
                position=self._get_element_position(elem)
            )
            data_objects.append(data_obj_ref)
        
        return data_objects
    
    def _extract_message_flows(self, root: ET.Element) -> List[BPMNMessageFlow]:
        """Извлечение message flows"""
        message_flows = []
        flows = root.findall('.//bpmn:messageFlow', self.bpmn_namespace)
        
        for flow in flows:
            message_flow = BPMNMessageFlow(
                message_flow_id=flow.get('id', ''),
                source_ref=flow.get('sourceRef', ''),
                target_ref=flow.get('targetRef', ''),
                name=flow.get('name', ''),
                message_ref=flow.get('messageRef', '')
            )
            message_flows.append(message_flow)
        
        return message_flows
    
    def _extract_swim_lanes(self, process_elem: ET.Element) -> List[Dict]:
        """Извлечение swim lanes (pools и lanes)"""
        swim_lanes = []
        
        # Pools
        pools = process_elem.findall('.//bpmn:pool', self.bpmn_namespace)
        for pool in pools:
            pool_info = {
                'pool_id': pool.get('id', ''),
                'name': pool.get('name', ''),
                'lanes': []
            }
            
            # Lanes
            lanes = pool.findall('.//bpmn:lane', self.bpmn_namespace)
            for lane in lanes:
                lane_info = {
                    'lane_id': lane.get('id', ''),
                    'name': lane.get('name', ''),
                    'flow_node_refs': lane.get('flowNodeRefs', '').split()
                }
                pool_info['lanes'].append(lane_info)
            
            swim_lanes.append(pool_info)
        
        return swim_lanes
    
    def _get_element_position(self, element: ET.Element) -> Dict[str, int]:
        """Получение позиции элемента из диаграммы"""
        # Пытаемся найти позицию в BPMNDiagram
        element_id = element.get('id', '')
        if not element_id:
            return {}
        
        try:
            # Ищем в диаграмме
            shape = element.getparent().getroot().find(
                f".//*[@bpmnElement='{element_id}']", 
                self.bpmn_namespace
            )
            if shape is not None:
                bounds = shape.find('.//dc:Bounds', self.bpmn_namespace)
                if bounds is not None:
                    return {
                        'x': int(bounds.get('x', 0)),
                        'y': int(bounds.get('y', 0)),
                        'width': int(bounds.get('width', 0)),
                        'height': int(bounds.get('height', 0))
                    }
        except:
            pass
        
        return {}
    
    def _get_condition_expression(self, flow_element: ET.Element) -> str:
        """Получение condition expression из sequence flow"""
        condition_expr = flow_element.find('.//bpmn:conditionExpression', self.bpmn_namespace)
        if condition_expr is not None:
            return condition_expr.text or ''
        return ''

class OpenAPIMapper:
    """Класс для маппинга BPMN task'ов с OpenAPI endpoints"""
    
    def __init__(self, openapi_spec_path: str):
        self.openapi_spec = self._load_openapi_spec(openapi_spec_path)
        self.endpoint_cache = {}
    
    def _load_openapi_spec(self, spec_path: str) -> Dict:
        """Загрузка OpenAPI спецификации"""
        try:
            with open(spec_path, 'r', encoding='utf-8') as f:
                return json.load(f)
        except Exception as e:
            print(f"❌ Error loading OpenAPI spec: {e}")
            return {}
    
    def map_bpmn_to_openapi(self, bpmn_task: Dict) -> Optional[EnhancedAPIEndpoint]:
        """Маппинг BPMN task с OpenAPI endpoint"""
        task_name = bpmn_task.get('task_name', '')
        task_id = bpmn_task.get('task_id', '')
        
        # Извлекаем HTTP метод и путь из названия задачи
        for method, pattern in {
            'GET': r'GET\s+([/\w\-\{\}\.]+)',
            'POST': r'POST\s+([/\w\-\{\}\.]+)', 
            'PUT': r'PUT\s+([/\w\-\{\}\.]+)',
            'DELETE': r'DELETE\s+([/\w\-\{\}\.]+)',
            'PATCH': r'PATCH\s+([/\w\-\{\}\.]+)'
        }.items():
            match = re.search(pattern, task_name, re.IGNORECASE)
            if match:
                api_path = match.group(1)
                openapi_endpoint = self._find_openapi_endpoint(method, api_path)
                
                if openapi_endpoint:
                    return self._create_enhanced_endpoint(
                        bpmn_task, method, api_path, openapi_endpoint
                    )
        
        return None
    
    def _find_openapi_endpoint(self, method: str, path: str) -> Optional[Dict]:
        """Поиск endpoint в OpenAPI спецификации"""
        if not self.openapi_spec or 'paths' not in self.openapi_spec:
            return None
        
        # Нормализуем путь
        normalized_path = self._normalize_path(path)
        
        # Ищем в путях
        for api_path, path_spec in self.openapi_spec['paths'].items():
            if self._paths_match(normalized_path, api_path):
                if method.lower() in path_spec:
                    return {
                        'path': api_path,
                        'method': method.lower(),
                        'spec': path_spec[method.lower()]
                    }
        
        return None
    
    def _normalize_path(self, path: str) -> str:
        """Нормализация пути для сравнения"""
        # Убираем дублирующие слеши
        path = re.sub(r'/+', '/', path)
        # Убираем начальный слеш если есть
        if path.startswith('/'):
            path = path[1:]
        return path
    
    def _paths_match(self, bpmn_path: str, openapi_path: str) -> bool:
        """Проверка соответствия путей BPMN и OpenAPI"""
        # Простое сравнение с поддержкой параметров
        bpmn_pattern = re.sub(r'\{[^}]+\}', '[^/]+', bpmn_path)
        openapi_pattern = re.sub(r'\{[^}]+\}', '[^/]+', openapi_path)
        
        try:
            return re.match(f'^{bpmn_pattern}$', openapi_pattern) is not None
        except:
            return bpmn_path == openapi_path
    
    def _create_enhanced_endpoint(self, bpmn_task: Dict, method: str, 
                                path: str, openapi_endpoint: Dict) -> EnhancedAPIEndpoint:
        """Создание расширенного endpoint с security анализом"""
        # Извлекаем параметры
        parameters = []
        if 'parameters' in openapi_endpoint['spec']:
            parameters = [p.get('name', '') for p in openapi_endpoint['spec']['parameters']]
        
        # Анализируем security риски
        security_analysis = self._analyze_security_risks(bpmn_task, path, method)
        
        # Определяем чувствительность данных
        data_sensitivity = self._assess_data_sensitivity(path, method)
        
        # Создаем test cases
        test_cases = self._generate_test_cases(bpmn_task, method, path, openapi_endpoint)
        
        return EnhancedAPIEndpoint(
            method=method,
            path=path,
            task_id=bpmn_task.get('task_id', ''),
            task_name=bpmn_task.get('task_name', ''),
            process_id=bpmn_task.get('process_id', ''),
            sequence=bpmn_task.get('sequence', 0),
            task_type=TaskType(bpmn_task.get('task_type', 'userTask')),
            description=openapi_endpoint['spec'].get('description', ''),
            parameters=parameters,
            requires_auth=security_analysis['requires_auth'],
            security_risk_level=security_analysis['risk_level'],
            owasp_categories=security_analysis['owasp_categories'],
            business_criticality=security_analysis['business_criticality'],
            test_cases=test_cases,
            data_sensitivity=data_sensitivity,
            compliance_requirements=security_analysis.get('compliance_requirements', [])
        )
    
    def _analyze_security_risks(self, bpmn_task: Dict, path: str, method: str) -> Dict:
        """Анализ security рисков для endpoint"""
        task_name = bpmn_task.get('task_name', '').lower()
        path_lower = path.lower()
        
        risk_score = 0
        owasp_categories = []
        requires_auth = False
        business_criticality = "medium"
        
        # Проверяем high-risk operations
        for operation in ['payment', 'transfer', 'account', 'card', 'balance']:
            if operation in task_name or operation in path_lower:
                risk_score += 3
                owasp_categories.extend(['A01:2021 – Broken Access Control', 
                                       'A02:2021 – Cryptographic Failures'])
        
        # Проверяем authentication
        auth_keywords = ['auth', 'login', 'token', 'credential']
        for keyword in auth_keywords:
            if keyword in task_name or keyword in path_lower:
                requires_auth = True
                risk_score += 2
                owasp_categories.append('A07:2021 – Identification and Authentication Failures')
        
        # Проверяем personal data
        data_keywords = ['personal', 'customer', 'client', 'user', 'profile']
        for keyword in data_keywords:
            if keyword in task_name or keyword in path_lower:
                risk_score += 2
                owasp_categories.extend(['A01:2021 – Broken Access Control',
                                       'A03:2021 – Injection'])
        
        # Определяем уровень риска
        if risk_score >= 8:
            risk_level = SecurityRiskLevel.CRITICAL
            business_criticality = "critical"
        elif risk_score >= 5:
            risk_level = SecurityRiskLevel.HIGH
            business_criticality = "high"
        elif risk_score >= 3:
            risk_level = SecurityRiskLevel.MEDIUM
            business_criticality = "medium"
        else:
            risk_level = SecurityRiskLevel.LOW
            business_criticality = "low"
        
        return {
            'risk_level': risk_level,
            'owasp_categories': list(set(owasp_categories)),
            'requires_auth': requires_auth,
            'business_criticality': business_criticality,
            'compliance_requirements': self._get_compliance_requirements(path, method)
        }
    
    def _assess_data_sensitivity(self, path: str, method: str) -> str:
        """Оценка чувствительности данных"""
        high_sensitivity_paths = ['account', 'card', 'payment', 'balance', 'personal']
        medium_sensitivity_paths = ['transaction', 'customer', 'profile']
        
        path_lower = path.lower()
        
        for keyword in high_sensitivity_paths:
            if keyword in path_lower:
                return "high"
        
        for keyword in medium_sensitivity_paths:
            if keyword in path_lower:
                return "medium"
        
        return "low"
    
    def _get_compliance_requirements(self, path: str, method: str) -> List[str]:
        """Определение compliance требований"""
        requirements = []
        path_lower = path.lower()
        
        if 'account' in path_lower or 'payment' in path_lower:
            requirements.extend(['PCI DSS', 'GDPR Article 32'])
        
        if 'personal' in path_lower or 'customer' in path_lower:
            requirements.extend(['GDPR Article 6', 'GDPR Article 7'])
        
        if 'auth' in path_lower or 'token' in path_lower:
            requirements.extend(['OWASP Authentication', 'NIST 800-63'])
        
        return list(set(requirements))
    
    def _generate_test_cases(self, bpmn_task: Dict, method: str, 
                           path: str, openapi_endpoint: Dict) -> List[TestCase]:
        """Генерация test cases для endpoint"""
        test_cases = []
        task_name = bpmn_task.get('task_name', '')
        
        # Happy path test
        happy_path = TestCase(
            test_id=f"happy_path_{hashlib.md5(f'{path}_{method}'.encode()).hexdigest()[:8]}",
            test_name=f"Happy Path - {task_name}",
            test_type="happy_path",
            api_endpoint=path,
            http_method=method,
            test_data=self._generate_valid_test_data(openapi_endpoint),
            expected_response={"status": 200},
            business_scenario=task_name
        )
        test_cases.append(happy_path)
        
        # Security tests based on OWASP
        if 'auth' in task_name.lower() or 'token' in task_name.lower():
            # Authentication tests
            security_tests = [
                TestCase(
                    test_id=f"auth_invalid_{hashlib.md5(f'{path}_{method}'.encode()).hexdigest()[:8]}",
                    test_name="Invalid Authentication Token",
                    test_type="security",
                    api_endpoint=path,
                    http_method=method,
                    test_data={"headers": {"Authorization": "Bearer invalid_token"}},
                    expected_response={"status": 401},
                    security_vectors=["A07:2021 – Identification and Authentication Failures"],
                    business_scenario=task_name
                ),
                TestCase(
                    test_id=f"auth_missing_{hashlib.md5(f'{path}_{method}'.encode()).hexdigest()[:8]}",
                    test_name="Missing Authentication",
                    test_type="security",
                    api_endpoint=path,
                    http_method=method,
                    test_data={},
                    expected_response={"status": 401},
                    security_vectors=["A07:2021 – Identification and Authentication Failures"],
                    business_scenario=task_name
                )
            ]
            test_cases.extend(security_tests)
        
        # Input validation tests
        validation_test = TestCase(
            test_id=f"validation_{hashlib.md5(f'{path}_{method}'.encode()).hexdigest()[:8]}",
            test_name="Input Validation - SQL Injection",
            test_type="security",
            api_endpoint=path,
            http_method=method,
            test_data={"query": "'; DROP TABLE users; --"},
            expected_response={"status": 400},
            security_vectors=["A03:2021 – Injection"],
            business_scenario=task_name
        )
        test_cases.append(validation_test)
        
        return test_cases
    
    def _generate_valid_test_data(self, openapi_endpoint: Dict) -> Dict:
        """Генерация валидных test данных на основе OpenAPI schema"""
        test_data = {}
        
        if 'requestBody' in openapi_endpoint['spec']:
            request_schema = openapi_endpoint['spec']['requestBody'].get('content', {})
            if 'application/json' in request_schema:
                schema = request_schema['application/json'].get('schema', {})
                test_data = self._generate_data_from_schema(schema)
        
        return test_data
    
    def _generate_data_from_schema(self, schema: Dict) -> Dict:
        """Генерация данных на основе JSON schema"""
        # Упрощенная генерация - в реальности нужна более сложная логика
        if '$ref' in schema:
            return {"example": "value"}
        
        data = {}
        if 'properties' in schema:
            for prop_name, prop_schema in schema['properties'].items():
                if prop_schema.get('type') == 'string':
                    data[prop_name] = f"test_{prop_name}"
                elif prop_schema.get('type') == 'number':
                    data[prop_name] = 123
                elif prop_schema.get('type') == 'boolean':
                    data[prop_name] = True
                elif prop_schema.get('type') == 'array':
                    data[prop_name] = ["item1", "item2"]
        
        return data

class BusinessLogicAnalyzer:
    """Анализатор бизнес-логики для выявления security-critical точек"""
    
    def __init__(self):
        self.workflow_patterns = {
            'authentication_flow': ['auth', 'login', 'token', 'credential'],
            'payment_flow': ['payment', 'transfer', 'money', 'amount'],
            'account_management': ['account', 'balance', 'card', 'profile'],
            'data_access': ['read', 'get', 'retrieve', 'fetch']
        }
    
    def analyze_business_logic(self, bpmn_structure: Dict, 
                             endpoints: List[EnhancedAPIEndpoint]) -> BusinessLogicAnalysis:
        """Анализ бизнес-логики процесса"""
        process_id = bpmn_structure.get('process_id', '')
        tasks = bpmn_structure.get('tasks', [])
        
        # Определяем workflow pattern
        workflow_pattern = self._identify_workflow_pattern(tasks, endpoints)
        
        # Находим критические операции
        critical_operations = self._find_critical_operations(tasks, endpoints)
        
        # Анализируем authentication flows
        auth_flows = self._analyze_authentication_flows(endpoints)
        
        # Находим authorization checkpoints
        auth_checkpoints = self._find_authorization_checkpoints(endpoints)
        
        # Анализируем точки валидации данных
        data_validation_points = self._analyze_data_validation_points(tasks, endpoints)
        
        # Анализируем обработку ошибок
        error_scenarios = self._analyze_error_handling(tasks)
        
        # Выявляем bottleneck'и производительности
        performance_issues = self._analyze_performance_bottlenecks(tasks, endpoints)
        
        return BusinessLogicAnalysis(
            process_id=process_id,
            workflow_pattern=workflow_pattern,
            critical_operations=critical_operations,
            authentication_flows=auth_flows,
            authorization_checkpoints=auth_checkpoints,
            data_validation_points=data_validation_points,
            error_handling_scenarios=error_scenarios,
            performance_bottlenecks=performance_issues
        )
    
    def _identify_workflow_pattern(self, tasks: List[Dict], 
                                 endpoints: List[EnhancedAPIEndpoint]) -> str:
        """Идентификация паттерна workflow"""
        task_names = [task.get('task_name', '').lower() for task in tasks]
        endpoint_info = [f"{ep.method} {ep.path}" for ep in endpoints]
        
        # Проверяем каждый паттерн
        for pattern_name, keywords in self.workflow_patterns.items():
            match_count = 0
            for task_name in task_names:
                for keyword in keywords:
                    if keyword in task_name:
                        match_count += 1
                        break
            
            if match_count >= 2:  # Минимум 2 совпадения
                return pattern_name
        
        return "custom_workflow"
    
    def _find_critical_operations(self, tasks: List[Dict], 
                                endpoints: List[EnhancedAPIEndpoint]) -> List[str]:
        """Поиск критических операций"""
        critical_ops = []
        
        # Критические операции на основе security риска
        for endpoint in endpoints:
            if endpoint.security_risk_level in [SecurityRiskLevel.HIGH, SecurityRiskLevel.CRITICAL]:
                critical_ops.append(f"{endpoint.method} {endpoint.path}")
        
        # Критические операции на основе task type
        for task in tasks:
            task_type = task.get('task_type', '')
            if task_type in ['serviceTask', 'businessRuleTask']:
                task_name = task.get('task_name', '')
                critical_ops.append(task_name)
        
        return list(set(critical_ops))
    
    def _analyze_authentication_flows(self, endpoints: List[EnhancedAPIEndpoint]) -> List[str]:
        """Анализ authentication flows"""
        auth_flows = []
        
        for endpoint in endpoints:
            if endpoint.requires_auth:
                auth_flows.append(f"{endpoint.method} {endpoint.path}")
        
        return auth_flows
    
    def _find_authorization_checkpoints(self, endpoints: List[EnhancedAPIEndpoint]) -> List[str]:
        """Поиск authorization checkpoints"""
        checkpoints = []
        
        for endpoint in endpoints:
            if endpoint.business_criticality in ['high', 'critical']:
                checkpoints.append(f"{endpoint.method} {endpoint.path}")
        
        return checkpoints
    
    def _analyze_data_validation_points(self, tasks: List[Dict], 
                                      endpoints: List[EnhancedAPIEndpoint]) -> List[str]:
        """Анализ точек валидации данных"""
        validation_points = []
        
        for endpoint in endpoints:
            if endpoint.parameters:
                validation_points.append(f"{endpoint.method} {endpoint.path} (parameters: {', '.join(endpoint.parameters)})")
        
        return validation_points
    
    def _analyze_error_handling(self, tasks: List[Dict]) -> List[str]:
        """Анализ обработки ошибок"""
        error_scenarios = []
        
        # Ищем task'и связанные с обработкой ошибок
        for task in tasks:
            task_name = task.get('task_name', '').lower()
            if any(keyword in task_name for keyword in ['error', 'exception', 'catch', 'finally']):
                error_scenarios.append(task.get('task_name', ''))
        
        return error_scenarios
    
    def _analyze_performance_bottlenecks(self, tasks: List[Dict], 
                                       endpoints: List[EnhancedAPIEndpoint]) -> List[str]:
        """Анализ bottleneck'ов производительности"""
        bottlenecks = []
        
        # Ищем API вызовы с high business criticality
        for endpoint in endpoints:
            if endpoint.business_criticality == 'critical':
                bottlenecks.append(f"Critical API: {endpoint.method} {endpoint.path}")
        
        # Ищем последовательные зависимые task'и
        for task in tasks:
            if len(task.get('incoming_flows', [])) > 1:
                bottlenecks.append(f"Parallel processing: {task.get('task_name', '')}")
        
        return bottlenecks

class SecurityTestGenerator:
    """Генератор security test cases на основе BPMN и OWASP methodology"""
    
    def __init__(self):
        self.owasp_top_10_2021 = {
            'A01': 'Broken Access Control',
            'A02': 'Cryptographic Failures',
            'A03': 'Injection',
            'A04': 'Insecure Design',
            'A05': 'Security Misconfiguration',
            'A06': 'Vulnerable and Outdated Components',
            'A07': 'Identification and Authentication Failures',
            'A08': 'Software and Data Integrity Failures',
            'A09': 'Security Logging and Monitoring Failures',
            'A10': 'Server-Side Request Forgery'
        }
    
    def generate_security_tests(self, endpoints: List[EnhancedAPIEndpoint], 
                              business_analysis: BusinessLogicAnalysis) -> List[TestCase]:
        """Генерация security test cases"""
        security_tests = []
        
        for endpoint in endpoints:
            # Генерируем тесты на основе OWASP categories
            for owasp_category in endpoint.owasp_categories:
                test_case = self._generate_owasp_test(endpoint, owasp_category)
                if test_case:
                    security_tests.append(test_case)
            
            # Дополнительные тесты на основе анализа рисков
            if endpoint.security_risk_level in [SecurityRiskLevel.HIGH, SecurityRiskLevel.CRITICAL]:
                high_risk_tests = self._generate_high_risk_tests(endpoint)
                security_tests.extend(high_risk_tests)
        
        return security_tests
    
    def _generate_owasp_test(self, endpoint: EnhancedAPIEndpoint, 
                           owasp_category: str) -> Optional[TestCase]:
        """Генерация теста на основе OWASP category"""
        if 'A01:2021' in owasp_category:  # Broken Access Control
            return TestCase(
                test_id=f"access_control_{hashlib.md5(f'{endpoint.path}_{endpoint.method}'.encode()).hexdigest()[:8]}",
                test_name="Broken Access Control - Unauthorized Access",
                test_type="security",
                api_endpoint=endpoint.path,
                http_method=endpoint.method,
                test_data={"headers": {"Authorization": "Bearer user_token"}},
                expected_response={"status": 403},
                security_vectors=[owasp_category],
                business_scenario=endpoint.task_name
            )
        
        elif 'A03:2021' in owasp_category:  # Injection
            return TestCase(
                test_id=f"injection_{hashlib.md5(f'{endpoint.path}_{endpoint.method}'.encode()).hexdigest()[:8]}",
                test_name="Injection Attack - SQL Injection",
                test_type="security",
                api_endpoint=endpoint.path,
                http_method=endpoint.method,
                test_data={"query": "'; DROP TABLE users; --"},
                expected_response={"status": 400},
                security_vectors=[owasp_category],
                business_scenario=endpoint.task_name
            )
        
        elif 'A07:2021' in owasp_category:  # Authentication Failures
            return TestCase(
                test_id=f"auth_fail_{hashlib.md5(f'{endpoint.path}_{endpoint.method}'.encode()).hexdigest()[:8]}",
                test_name="Authentication Failure - Weak Password",
                test_type="security",
                api_endpoint=endpoint.path,
                http_method=endpoint.method,
                test_data={"password": "123"},
                expected_response={"status": 401},
                security_vectors=[owasp_category],
                business_scenario=endpoint.task_name
            )
        
        return None
    
    def _generate_high_risk_tests(self, endpoint: EnhancedAPIEndpoint) -> List[TestCase]:
        """Генерация тестов для high-risk endpoints"""
        high_risk_tests = []
        
        # Тест на rate limiting
        rate_limit_test = TestCase(
            test_id=f"rate_limit_{hashlib.md5(f'{endpoint.path}_{endpoint.method}'.encode()).hexdigest()[:8]}",
            test_name="High Risk - Rate Limiting Test",
            test_type="security",
            api_endpoint=endpoint.path,
            http_method=endpoint.method,
            test_data={"headers": {"Authorization": "Bearer valid_token"}},
            expected_response={"status": 429},
            security_vectors=["Rate Limiting Bypass"],
            business_scenario=endpoint.task_name
        )
        high_risk_tests.append(rate_limit_test)
        
        # Тест на input validation для high-sensitivity data
        if endpoint.data_sensitivity in ['high', 'critical']:
            data_test = TestCase(
                test_id=f"data_validation_{hashlib.md5(f'{endpoint.path}_{endpoint.method}'.encode()).hexdigest()[:8]}",
                test_name="High Sensitivity Data - Input Validation",
                test_type="security",
                api_endpoint=endpoint.path,
                http_method=endpoint.method,
                test_data={"personal_data": "<script>alert('xss')</script>"},
                expected_response={"status": 400},
                security_vectors=["XSS", "Input Validation"],
                business_scenario=endpoint.task_name
            )
            high_risk_tests.append(data_test)
        
        return high_risk_tests

class ComprehensiveBPMNAnalyzer:
    """Основной класс для комплексного анализа BPMN процессов"""
    
    def __init__(self, openapi_spec_path: str):
        self.bpmn_parser = EnhancedBPMNParser()
        self.openapi_mapper = OpenAPIMapper(openapi_spec_path)
        self.business_analyzer = BusinessLogicAnalyzer()
        self.security_generator = SecurityTestGenerator()
        
    def analyze_bpmn_file(self, bpmn_file_path: str) -> Dict:
        """Полный анализ BPMN файла"""
        print(f"🔍 Analyzing BPMN file: {bpmn_file_path}")
        
        # 1. Парсинг структуры BPMN
        bpmn_structure = self.bpmn_parser.parse_bpmn_structure(bpmn_file_path)
        if not bpmn_structure:
            return {}
        
        # 2. Извлечение и маппинг API endpoints
        endpoints = self._extract_and_map_endpoints(bpmn_structure)
        
        # 3. Анализ бизнес-логики
        business_analysis = self.business_analyzer.analyze_business_logic(
            bpmn_structure, endpoints
        )
        
        # 4. Генерация security tests
        security_tests = self.security_generator.generate_security_tests(
            endpoints, business_analysis
        )
        
        # 5. Создание security assessment points
        security_assessment = self._create_security_assessment_points(
            endpoints, business_analysis
        )
        
        return {
            'file_path': bpmn_file_path,
            'analysis_timestamp': datetime.now().isoformat(),
            'bpmn_structure': self._serialize_bpmn_structure(bpmn_structure),
            'api_endpoints': [asdict(ep) for ep in endpoints],
            'business_analysis': asdict(business_analysis),
            'security_tests': [asdict(test) for test in security_tests],
            'security_assessment_points': [asdict(point) for point in security_assessment],
            'summary': self._create_analysis_summary(
                bpmn_structure, endpoints, business_analysis, security_tests
            )
        }
    
    def _extract_and_map_endpoints(self, bpmn_structure: Dict) -> List[EnhancedAPIEndpoint]:
        """Извлечение и маппинг API endpoints"""
        endpoints = []
        tasks = bpmn_structure.get('tasks', [])
        
        for task in tasks:
            # Добавляем process_id к task
            task['process_id'] = bpmn_structure.get('process_id', '')
            
            endpoint = self.openapi_mapper.map_bpmn_to_openapi(task)
            if endpoint:
                endpoints.append(endpoint)
        
        return endpoints
    
    def _create_security_assessment_points(self, endpoints: List[EnhancedAPIEndpoint],
                                         business_analysis: BusinessLogicAnalysis) -> List[SecurityAssessmentPoint]:
        """Создание точек для security assessment"""
        assessment_points = []
        
        for endpoint in endpoints:
            for owasp_category in endpoint.owasp_categories:
                point = SecurityAssessmentPoint(
                    point_id=f"asp_{hashlib.md5(f'{endpoint.path}_{endpoint.method}_{owasp_category}'.encode()).hexdigest()[:8]}",
                    process_step=endpoint.task_name,
                    api_endpoint=f"{endpoint.method} {endpoint.path}",
                    risk_level=endpoint.security_risk_level,
                    owasp_category=owasp_category,
                    description=f"Security testing required for {endpoint.task_name}",
                    test_vectors=self._generate_test_vectors(endpoint, owasp_category),
                    mitigation_suggestions=self._generate_mitigation_suggestions(endpoint, owasp_category)
                )
                assessment_points.append(point)
        
        return assessment_points
    
    def _generate_test_vectors(self, endpoint: EnhancedAPIEndpoint, 
                             owasp_category: str) -> List[str]:
        """Генерация test vectors"""
        vectors = []
        
        if 'A01:2021' in owasp_category:  # Broken Access Control
            vectors.extend([
                "Test with insufficient privileges",
                "Test with expired token",
                "Test with manipulated JWT",
                "Test direct object references"
            ])
        
        elif 'A03:2021' in owasp_category:  # Injection
            vectors.extend([
                "SQL injection payloads",
                "NoSQL injection payloads", 
                "XSS payloads",
                "Command injection payloads"
            ])
        
        elif 'A07:2021' in owasp_category:  # Authentication Failures
            vectors.extend([
                "Weak password attempts",
                "Brute force attacks",
                "Session hijacking",
                "Authentication bypass attempts"
            ])
        
        return vectors
    
    def _generate_mitigation_suggestions(self, endpoint: EnhancedAPIEndpoint,
                                       owasp_category: str) -> List[str]:
        """Генерация предложений по mitigation"""
        suggestions = []
        
        if 'A01:2021' in owasp_category:  # Broken Access Control
            suggestions.extend([
                "Implement proper access controls",
                "Use role-based permissions",
                "Validate user permissions on each request",
                "Implement principle of least privilege"
            ])
        
        elif 'A03:2021' in owasp_category:  # Injection
            suggestions.extend([
                "Use parameterized queries",
                "Implement input validation",
                "Use output encoding",
                "Implement WAF protection"
            ])
        
        elif 'A07:2021' in owasp_category:  # Authentication Failures
            suggestions.extend([
                "Implement strong password policies",
                "Use multi-factor authentication",
                "Implement account lockout mechanisms",
                "Use secure session management"
            ])
        
        return suggestions
    
    def _serialize_bpmn_structure(self, structure: Dict) -> Dict:
        """Сериализация BPMN структуры для JSON"""
        serialized = structure.copy()
        
        # Преобразуем объекты в словари
        if 'gateways' in serialized:
            serialized['gateways'] = [asdict(gw) for gw in serialized['gateways']]
        
        if 'events' in serialized:
            serialized['events'] = [asdict(ev) for ev in serialized['events']]
        
        if 'data_objects' in serialized:
            serialized['data_objects'] = [asdict(do) for do in serialized['data_objects']]
        
        if 'message_flows' in serialized:
            serialized['message_flows'] = [asdict(mf) for mf in serialized['message_flows']]
        
        return serialized
    
    def _create_analysis_summary(self, bpmn_structure: Dict, endpoints: List[EnhancedAPIEndpoint],
                               business_analysis: BusinessLogicAnalysis, 
                               security_tests: List[TestCase]) -> Dict:
        """Создание сводки анализа"""
        
        # Подсчет security рисков
        risk_distribution = {}
        for endpoint in endpoints:
            risk_level = endpoint.security_risk_level.value
            risk_distribution[risk_level] = risk_distribution.get(risk_level, 0) + 1
        
        # OWASP categories coverage
        owasp_coverage = {}
        for endpoint in endpoints:
            for category in endpoint.owasp_categories:
                owasp_coverage[category] = owasp_coverage.get(category, 0) + 1
        
        # Critical operations count
        critical_operations = len(business_analysis.critical_operations)
        
        return {
            'total_tasks': len(bpmn_structure.get('tasks', [])),
            'total_api_endpoints': len(endpoints),
            'security_risk_distribution': risk_distribution,
            'owasp_categories_coverage': owasp_coverage,
            'critical_operations_count': critical_operations,
            'total_security_tests_generated': len(security_tests),
            'authentication_required_endpoints': sum(1 for ep in endpoints if ep.requires_auth),
            'high_sensitivity_data_endpoints': sum(1 for ep in endpoints if ep.data_sensitivity in ['high', 'critical']),
            'workflow_pattern_identified': business_analysis.workflow_pattern,
            'compliance_requirements': list(set(req for ep in endpoints for req in ep.compliance_requirements))
        }
    
    def analyze_directory(self, bpmn_directory: str) -> Dict:
        """Анализ всех BPMN файлов в директории"""
        bpmn_path = Path(bpmn_directory)
        if not bpmn_path.exists():
            print(f"❌ Directory not found: {bpmn_directory}")
            return {}
        
        bpmn_files = list(bpmn_path.glob("*.bpmn"))
        if not bpmn_files:
            print(f"❌ No BPMN files found in: {bpmn_directory}")
            return {}
        
        print(f"🔍 Found {len(bpmn_files)} BPMN files to analyze")
        
        all_analyses = []
        for bpmn_file in bpmn_files:
            analysis = self.analyze_bpmn_file(str(bpmn_file))
            if analysis:
                all_analyses.append(analysis)
        
        # Создаем сводный отчет
        summary_report = self._create_summary_report(all_analyses)
        
        return {
            'analysis_timestamp': datetime.now().isoformat(),
            'total_files_analyzed': len(all_analyses),
            'individual_analyses': all_analyses,
            'summary_report': summary_report
        }
    
    def _create_summary_report(self, analyses: List[Dict]) -> Dict:
        """Создание сводного отчета по всем анализам"""
        if not analyses:
            return {}
        
        total_endpoints = sum(len(a.get('api_endpoints', [])) for a in analyses)
        total_security_tests = sum(len(a.get('security_tests', [])) for a in analyses)
        total_critical_endpoints = sum(
            1 for a in analyses 
            for ep in a.get('api_endpoints', []) 
            if ep.get('security_risk_level') in ['high', 'critical']
        )
        
        # Объединяем OWASP categories
        all_owasp_categories = {}
        for analysis in analyses:
            for category, count in analysis.get('summary', {}).get('owasp_categories_coverage', {}).items():
                all_owasp_categories[category] = all_owasp_categories.get(category, 0) + count
        
        return {
            'total_api_endpoints_found': total_endpoints,
            'total_security_tests_generated': total_security_tests,
            'total_critical_endpoints': total_critical_endpoints,
            'overall_owasp_coverage': all_owasp_categories,
            'files_processed': len(analyses),
            'recommendations': self._generate_global_recommendations(analyses)
        }
    
    def _generate_global_recommendations(self, analyses: List[Dict]) -> List[str]:
        """Генерация глобальных рекомендаций"""
        recommendations = []
        
        # Анализируем общие паттерны
        total_endpoints = sum(len(a.get('api_endpoints', [])) for a in analyses)
        critical_endpoints = sum(
            1 for a in analyses 
            for ep in a.get('api_endpoints', []) 
            if ep.get('security_risk_level') in ['high', 'critical']
        )
        
        if critical_endpoints > total_endpoints * 0.3:
            recommendations.append("High percentage of critical endpoints detected. Consider implementing additional security controls.")
        
        auth_endpoints = sum(
            1 for a in analyses 
            for ep in a.get('api_endpoints', []) 
            if ep.get('requires_auth')
        )
        
        if auth_endpoints < total_endpoints * 0.5:
            recommendations.append("Consider implementing authentication for more endpoints to improve security posture.")
        
        return recommendations
    
    def export_analysis_to_json(self, analysis: Dict, output_file: str) -> bool:
        """Экспорт анализа в JSON файл"""
        try:
            with open(output_file, 'w', encoding='utf-8') as f:
                json.dump(analysis, f, indent=2, ensure_ascii=False, default=str)
            print(f"📊 Analysis exported to: {output_file}")
            return True
        except Exception as e:
            print(f"❌ Error exporting analysis: {e}")
            return False
    
    def generate_security_report(self, analysis: Dict, output_file: str) -> bool:
        """Генерация детального security отчета"""
        try:
            report_content = self._create_security_report_content(analysis)
            
            with open(output_file, 'w', encoding='utf-8') as f:
                f.write(report_content)
            
            print(f"🛡️ Security report generated: {output_file}")
            return True
        except Exception as e:
            print(f"❌ Error generating security report: {e}")
            return False
    
    def _create_security_report_content(self, analysis: Dict) -> str:
        """Создание содержимого security отчета"""
        report = []
        report.append("# BPMN Security Analysis Report")
        report.append(f"Generated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        report.append("=" * 50)
        
        # Executive Summary
        if 'summary_report' in analysis:
            summary = analysis['summary_report']
            report.append("\n## Executive Summary")
            report.append(f"- Files Analyzed: {summary.get('files_processed', 0)}")
            report.append(f"- API Endpoints Found: {summary.get('total_api_endpoints_found', 0)}")
            report.append(f"- Security Tests Generated: {summary.get('total_security_tests_generated', 0)}")
            report.append(f"- Critical Endpoints: {summary.get('total_critical_endpoints', 0)}")
        
        # Individual Analysis
        report.append("\n## Individual Process Analysis")
        for i, individual_analysis in enumerate(analysis.get('individual_analyses', []), 1):
            report.append(f"\n### Process {i}: {individual_analysis.get('file_path', 'Unknown')}")
            
            # API Endpoints
            endpoints = individual_analysis.get('api_endpoints', [])
            if endpoints:
                report.append("\n#### API Endpoints:")
                for ep in endpoints:
                    risk_indicator = "🔴" if ep.get('security_risk_level') in ['high', 'critical'] else "🟡" if ep.get('security_risk_level') == 'medium' else "🟢"
                    report.append(f"- {risk_indicator} [{ep.get('method', '')}] {ep.get('path', '')} - {ep.get('security_risk_level', 'unknown').title()}")
            
            # Security Tests
            security_tests = individual_analysis.get('security_tests', [])
            if security_tests:
                report.append(f"\n#### Security Tests Generated: {len(security_tests)}")
                for test in security_tests:
                    report.append(f"- {test.get('test_name', 'Unknown Test')} ({test.get('test_type', 'unknown')})")
        
        # OWASP Coverage
        if 'summary_report' in analysis and 'overall_owasp_coverage' in analysis['summary_report']:
            report.append("\n## OWASP Top 10 2021 Coverage")
            owasp_coverage = analysis['summary_report']['overall_owasp_coverage']
            for category, count in owasp_coverage.items():
                report.append(f"- {category}: {count} test vectors")
        
        # Recommendations
        if 'summary_report' in analysis and 'recommendations' in analysis['summary_report']:
            report.append("\n## Security Recommendations")
            for rec in analysis['summary_report']['recommendations']:
                report.append(f"- {rec}")
        
        return "\n".join(report)

def main():
    """Главная функция для демонстрации работы Enhanced BPMN API Extractor"""
    
    print("🚀 Enhanced BPMN API Extractor - Comprehensive Business Process Security Analysis")
    print("=" * 80)
    
    # Инициализируем анализатор
    openapi_spec_path = "guide/openapi.json"
    analyzer = ComprehensiveBPMNAnalyzer(openapi_spec_path)
    
    # Анализируем директорию с BPMN файлами
    bpmn_directory = "guide/bpmn"
    
    print(f"🔍 Starting comprehensive analysis of BPMN files in: {bpmn_directory}")
    complete_analysis = analyzer.analyze_directory(bpmn_directory)
    
    if not complete_analysis:
        print("❌ No analysis results obtained")
        return False
    
    # Экспортируем полный анализ
    analysis_file = "BPMN_COMPREHENSIVE_ANALYSIS.json"
    if analyzer.export_analysis_to_json(complete_analysis, analysis_file):
        print(f"✅ Comprehensive analysis exported to: {analysis_file}")
    
    # Генерируем security отчет
    security_report_file = "BPMN_SECURITY_REPORT.md"
    if analyzer.generate_security_report(complete_analysis, security_report_file):
        print(f"🛡️ Security report generated: {security_report_file}")
    
    # Показываем summary
    if 'summary_report' in complete_analysis:
        summary = complete_analysis['summary_report']
        print("\n📊 ANALYSIS SUMMARY:")
        print("-" * 40)
        print(f"Files Analyzed: {summary.get('files_processed', 0)}")
        print(f"API Endpoints Found: {summary.get('total_api_endpoints_found', 0)}")
        print(f"Security Tests Generated: {summary.get('total_security_tests_generated', 0)}")
        print(f"Critical Endpoints: {summary.get('total_critical_endpoints', 0)}")
        
        if 'overall_owasp_coverage' in summary:
            print(f"\nOWASP Top 10 Coverage:")
            for category, count in summary['overall_owasp_coverage'].items():
                print(f"  - {category}: {count} tests")
        
        if 'recommendations' in summary and summary['recommendations']:
            print(f"\n🔍 Security Recommendations:")
            for rec in summary['recommendations']:
                print(f"  - {rec}")
    
    print("\n✅ Enhanced BPMN API Extractor analysis completed successfully!")
    return True

if __name__ == "__main__":
    main()