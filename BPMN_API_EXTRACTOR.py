#!/usr/bin/env python3
"""
🔍 BPMN API EXTRACTOR
Извлекает API вызовы из BPMN процессов для security тестирования

Анализирует BPMN файлы и извлекает REST API endpoints из названий задач,
затем маппит их с OpenAPI спецификацией для создания security тестов.
"""

import re
import json
import xml.etree.ElementTree as ET
from pathlib import Path
from typing import List, Dict, Optional, Tuple
from dataclasses import dataclass

@dataclass
class APIEndpoint:
    """Структура для хранения информации об API endpoint"""
    method: str
    path: str
    task_id: str
    task_name: str
    process_id: str
    sequence: int
    description: str = ""
    parameters: List[str] = None
    requires_auth: bool = False

@dataclass 
class BPMNProcess:
    """Структура для хранения информации о BPMN процессе"""
    process_id: str
    file_path: str
    endpoints: List[APIEndpoint]
    total_tasks: int
    start_event: str
    end_event: str

class BPMNAPIExtractor:
    """Основной класс для извлечения API endpoints из BPMN процессов"""
    
    def __init__(self):
        self.processes: List[BPMNProcess] = []
        self.api_patterns = {
            'GET': r'GET\s+([/\w\-\{\}]+)',
            'POST': r'POST\s+([/\w\-\{\}]+)', 
            'PUT': r'PUT\s+([/\w\-\{\}]+)',
            'DELETE': r'DELETE\s+([/\w\-\{\}]+)',
            'PATCH': r'PATCH\s+([/\w\-\{\}]+)'
        }
        
        # Ключевые слова для определения аутентификации
        self.auth_keywords = [
            'auth', 'token', 'login', 'password', 'безопасность', 'security'
        ]
    
    def extract_api_calls_from_task(self, task_element: ET.Element) -> Optional[APIEndpoint]:
        """Извлекает API вызов из названия задачи BPMN"""
        
        # Получаем название задачи
        task_name = task_element.get('name', '')
        if not task_name:
            return None
            
        # Ищем HTTP метод и путь в названии задачи
        for method, pattern in self.api_patterns.items():
            match = re.search(pattern, task_name, re.IGNORECASE)
            if match:
                path = match.group(1)
                task_id = task_element.get('id', '')
                process_id = task_element.getparent().get('id', '') if task_element.getparent() is not None else ''
                
                # Определяем требует ли аутентификации
                requires_auth = any(keyword in task_name.lower() for keyword in self.auth_keywords)
                
                # Извлекаем параметры из path
                parameters = re.findall(r'\{([^}]+)\}', path)
                
                # Определяем последовательность
                sequence = self._calculate_sequence(task_element)
                
                return APIEndpoint(
                    method=method,
                    path=path,
                    task_id=task_id,
                    task_name=task_name,
                    process_id=process_id,
                    sequence=sequence,
                    description=task_name,
                    parameters=parameters,
                    requires_auth=requires_auth
                )
        
        return None
    
    def _calculate_sequence(self, task_element: ET.Element) -> int:
        """Вычисляет последовательность задачи в процессе"""
        sequence = 0
        current = task_element
        while current is not None:
            if current.tag.endswith('sequenceFlow'):
                sequence += 1
            current = current.getprevious()
        return sequence
    
    def parse_bpmn_file(self, file_path: str) -> Optional[BPMNProcess]:
        """Парсит BPMN файл и извлекает API endpoints"""
        
        try:
            tree = ET.parse(file_path)
            root = tree.getroot()
            
            # Определяем namespace
            namespace = {'bpmn': 'http://www.omg.org/spec/BPMN/20100524/MODEL'}
            
            # Находим процесс
            process_elem = root.find('.//bpmn:process', namespace)
            if process_elem is None:
                print(f"❌ Процесс не найден в файле: {file_path}")
                return None
            
            process_id = process_elem.get('id', '')
            
            # Извлекаем start и end events
            start_event = ""
            end_event = ""
            
            start_elem = process_elem.find('.//bpmn:startEvent', namespace)
            if start_elem is not None:
                start_event = start_elem.get('name', '')
            
            end_elem = process_elem.find('.//bpmn:endEvent', namespace) 
            if end_elem is not None:
                end_event = end_elem.get('name', '')
            
            # Извлекаем задачи с API вызовами
            endpoints = []
            tasks = process_elem.findall('.//bpmn:task', namespace)
            
            for task in tasks:
                endpoint = self.extract_api_calls_from_task(task)
                if endpoint:
                    endpoints.append(endpoint)
            
            if not endpoints:
                print(f"⚠️  API endpoints не найдены в процессе: {process_id}")
                return None
            
            print(f"✅ Извлечено {len(endpoints)} API endpoints из {file_path}")
            
            return BPMNProcess(
                process_id=process_id,
                file_path=file_path,
                endpoints=endpoints,
                total_tasks=len(tasks),
                start_event=start_event,
                end_event=end_event
            )
            
        except Exception as e:
            print(f"❌ Ошибка при парсинге BPMN файла {file_path}: {e}")
            return None
    
    def extract_from_directory(self, bpmn_dir: str) -> List[BPMNProcess]:
        """Извлекает API endpoints из всех BPMN файлов в директории"""
        
        bpmn_path = Path(bpmn_dir)
        if not bpmn_path.exists():
            print(f"❌ Директория не найдена: {bpmn_dir}")
            return []
        
        bpmn_files = list(bpmn_path.glob("*.bpmn"))
        if not bpmn_files:
            print(f"❌ BPMN файлы не найдены в директории: {bpmn_dir}")
            return []
        
        print(f"🔍 Найдено {len(bpmn_files)} BPMN файлов для анализа")
        
        processes = []
        for bpmn_file in bpmn_files:
            process = self.parse_bpmn_file(str(bpmn_file))
            if process:
                processes.append(process)
        
        self.processes = processes
        return processes
    
    def get_extracted_endpoints(self) -> List[APIEndpoint]:
        """Возвращает все извлеченные API endpoints"""
        all_endpoints = []
        for process in self.processes:
            all_endpoints.extend(process.endpoints)
        return all_endpoints
    
    def get_process_summary(self) -> Dict:
        """Возвращает сводку по извлеченным процессам"""
        return {
            "total_processes": len(self.processes),
            "total_endpoints": len(self.get_extracted_endpoints()),
            "processes": [
                {
                    "process_id": process.process_id,
                    "file_path": process.file_path,
                    "endpoints_count": len(process.endpoints),
                    "total_tasks": process.total_tasks,
                    "start_event": process.start_event,
                    "end_event": process.end_event,
                    "auth_required_count": sum(1 for ep in process.endpoints if ep.requires_auth)
                }
                for process in self.processes
            ],
            "method_distribution": self._get_method_distribution(),
            "authentication_analysis": self._get_auth_analysis()
        }
    
    def _get_method_distribution(self) -> Dict[str, int]:
        """Получает распределение HTTP методов"""
        distribution = {}
        for endpoint in self.get_extracted_endpoints():
            method = endpoint.method
            distribution[method] = distribution.get(method, 0) + 1
        return distribution
    
    def _get_auth_analysis(self) -> Dict:
        """Анализирует требования к аутентификации"""
        endpoints = self.get_extracted_endpoints()
        auth_endpoints = [ep for ep in endpoints if ep.requires_auth]
        
        return {
            "total_endpoints": len(endpoints),
            "auth_required": len(auth_endpoints),
            "auth_percentage": round(len(auth_endpoints) / len(endpoints) * 100, 2) if endpoints else 0,
            "auth_endpoints": [ep.path for ep in auth_endpoints]
        }
    
    def export_to_json(self, output_file: str) -> bool:
        """Экспортирует результаты в JSON файл"""
        try:
            export_data = {
                "extraction_timestamp": "2025-11-09T18:07:00Z",
                "summary": self.get_process_summary(),
                "processes": []
            }
            
            for process in self.processes:
                process_data = {
                    "process_id": process.process_id,
                    "file_path": process.file_path,
                    "start_event": process.start_event,
                    "end_event": process.end_event,
                    "endpoints": []
                }
                
                for endpoint in process.endpoints:
                    endpoint_data = {
                        "method": endpoint.method,
                        "path": endpoint.path,
                        "task_id": endpoint.task_id,
                        "task_name": endpoint.task_name,
                        "sequence": endpoint.sequence,
                        "description": endpoint.description,
                        "parameters": endpoint.parameters,
                        "requires_auth": endpoint.requires_auth
                    }
                    process_data["endpoints"].append(endpoint_data)
                
                export_data["processes"].append(process_data)
            
            with open(output_file, 'w', encoding='utf-8') as f:
                json.dump(export_data, f, indent=2, ensure_ascii=False)
            
            print(f"📊 Результаты экспортированы в: {output_file}")
            return True
            
        except Exception as e:
            print(f"❌ Ошибка при экспорте: {e}")
            return False

def main():
    """Пример использования BPMN API Extractor"""
    
    print("🚀 Запуск BPMN API Extractor")
    print("=" * 60)
    
    # Инициализируем экстрактор
    extractor = BPMNAPIExtractor()
    
    # Извлекаем API endpoints из директории с BPMN файлами
    bpmn_directory = "guide/bpmn"
    processes = extractor.extract_from_directory(bpmn_directory)
    
    if not processes:
        print("❌ Процессы не найдены для анализа")
        return False
    
    # Получаем сводку
    summary = extractor.get_process_summary()
    
    print("\n📊 СВОДКА ИЗВЛЕЧЕНИЯ:")
    print("-" * 40)
    print(f"Всего процессов: {summary['total_processes']}")
    print(f"Всего API endpoints: {summary['total_endpoints']}")
    print(f"Распределение методов: {summary['method_distribution']}")
    print(f"Требуют аутентификации: {summary['authentication_analysis']['auth_required']} ({summary['authentication_analysis']['auth_percentage']}%)")
    
    # Показываем детали каждого процесса
    print("\n🔍 ДЕТАЛИ ПРОЦЕССОВ:")
    print("-" * 40)
    for process in processes:
        print(f"\n📋 Процесс: {process.process_id}")
        print(f"📁 Файл: {process.file_path}")
        print(f"🎯 Начало: {process.start_event}")
        print(f"🏁 Конец: {process.end_event}")
        print(f"🔗 API Endpoints ({len(process.endpoints)}):")
        
        for endpoint in process.endpoints:
            auth_indicator = "🔐" if endpoint.requires_auth else "🔓"
            params_info = f" (параметры: {', '.join(endpoint.parameters)})" if endpoint.parameters else ""
            print(f"  {auth_indicator} [{endpoint.method}] {endpoint.path}{params_info}")
            print(f"    └── {endpoint.task_name}")
    
    # Экспортируем в JSON
    output_file = "BPMN_API_EXTRACTION_RESULTS.json"
    if extractor.export_to_json(output_file):
        print(f"\n✅ Анализ завершен успешно!")
        print(f"📄 Результаты сохранены в: {output_file}")
        return True
    else:
        print(f"\n❌ Ошибка при сохранении результатов")
        return False

if __name__ == "__main__":
    main()