// Security Orchestrator Frontend JavaScript - Material Design inspired

let currentFilter = 'all';
let searchQuery = '';

$(document).ready(function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        $('.alert').fadeOut('slow');
    }, 5000);

    // Initialize home page functionality
    if (window.location.pathname === '/' || window.location.pathname === '/home') {
        initializeHomePage();
    }

    // File size validation
    $('input[type="file"]').on('change', function() {
        const file = this.files[0];
        if (file) {
            const maxSize = 10 * 1024 * 1024; // 10MB
            if (file.size > maxSize) {
                alert('File size must be less than 10MB');
                this.value = '';
            }
        }
    });

    // Form validation
    $('form').on('submit', function(e) {
        const requiredFields = $(this).find('[required]');
        let isValid = true;

        requiredFields.each(function() {
            if (!$(this).val().trim()) {
                $(this).addClass('is-invalid');
                isValid = false;
            } else {
                $(this).removeClass('is-invalid');
            }
        });

        if (!isValid) {
            e.preventDefault();
            alert('Please fill in all required fields.');
        }
    });

    // Remove validation styling on input
    $('input, select, textarea').on('input change', function() {
        if ($(this).val().trim()) {
            $(this).removeClass('is-invalid');
        }
    });
});

// WebSocket connection for real-time updates
class ExecutionMonitor {
    constructor(executionId) {
        this.executionId = executionId;
        this.statusElement = $(`#execution-status-${executionId}`);
        this.progressElement = $(`#execution-progress-${executionId}`);
    }

    connect() {
        if (typeof WebSocket === 'undefined') {
            console.warn('WebSocket not supported');
            return;
        }

        // In a real implementation, you'd connect to your WebSocket endpoint
        // For now, we'll simulate updates
        this.simulateUpdates();
    }

    simulateUpdates() {
        const statuses = ['PENDING', 'RUNNING', 'RUNNING', 'RUNNING', 'COMPLETED'];
        let index = 0;

        const interval = setInterval(() => {
            if (index < statuses.length) {
                this.updateStatus(statuses[index]);
                index++;
            } else {
                clearInterval(interval);
            }
        }, 2000);
    }

    updateStatus(status) {
        if (this.statusElement.length) {
            this.statusElement.text(status);

            // Update badge color
            this.statusElement.removeClass('bg-secondary bg-warning bg-info bg-success bg-danger');
            switch (status) {
                case 'PENDING':
                    this.statusElement.addClass('bg-secondary');
                    break;
                case 'RUNNING':
                    this.statusElement.addClass('bg-warning');
                    break;
                case 'COMPLETED':
                    this.statusElement.addClass('bg-success');
                    break;
                case 'FAILED':
                    this.statusElement.addClass('bg-danger');
                    break;
            }
        }

        if (this.progressElement.length) {
            const progress = this.getProgressForStatus(status);
            this.progressElement.css('width', progress + '%').attr('aria-valuenow', progress);
        }
    }

    getProgressForStatus(status) {
        switch (status) {
            case 'PENDING': return 0;
            case 'RUNNING': return 50;
            case 'COMPLETED': return 100;
            case 'FAILED': return 100;
            default: return 0;
        }
    }
}

// Utility functions
function formatDateTime(instant) {
    if (!instant) return '';
    const date = new Date(instant);
    return date.toLocaleString();
}

function showLoading(button) {
    const originalText = button.html();
    button.prop('disabled', true).html('<span class="spinner-border spinner-border-sm me-2"></span>Loading...');
    return originalText;
}

function hideLoading(button, originalText) {
    button.prop('disabled', false).html(originalText);
}

// Home page functionality
function initializeHomePage() {
    // Initialize filter chips
    $('.filter-chips .chip').on('click', function() {
        $('.filter-chips .chip').removeClass('selected');
        $(this).addClass('selected');
        setFilter($(this).text().toLowerCase());
    });

    // Set initial filter
    $('.filter-chips .chip').first().addClass('selected');
}

function filterProcesses() {
    searchQuery = $('#searchInput').val().toLowerCase();
    applyFilters();
}

function setFilter(filter) {
    currentFilter = filter;
    applyFilters();
}

function applyFilters() {
    $('.process-card').each(function() {
        const card = $(this);
        const processName = card.find('h6').text().toLowerCase();
        const status = card.find('.badge').text().toLowerCase();

        const matchesSearch = processName.includes(searchQuery);
        const matchesFilter = currentFilter === 'all' || status === currentFilter;

        if (matchesSearch && matchesFilter) {
            card.show();
        } else {
            card.hide();
        }
    });

    // Show empty state if no results
    const visibleCards = $('.process-card:visible');
    const emptyState = $('#processList .text-center');

    if (visibleCards.length === 0 && !emptyState.is(':visible')) {
        $('#processList').append(`
            <div class="text-center text-muted py-5">
                <i class="material-icons" style="font-size: 64px; color: #e0e0e0;">search_off</i>
                <h4 class="mt-3">No processes found</h4>
                <p>Try adjusting your search or filter criteria</p>
            </div>
        `);
    } else {
        emptyState.remove();
    }
}

function refreshProcesses() {
    const refreshBtn = $('button[onclick="refreshProcesses()"]');
    const originalText = refreshBtn.html();
    showLoading(refreshBtn);

    // Simulate API call
    setTimeout(() => {
        hideLoading(refreshBtn, originalText);
        // In real implementation, reload the page or fetch new data
        location.reload();
    }, 1000);
}

function openProcess(processId) {
    window.location.href = `/processes/${processId}`;
}

function showProcessMenu(event, processId) {
    event.stopPropagation();

    // Create modal bottom sheet for process actions
    const modal = $(`
        <div class="modal fade" id="processMenuModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Process Actions</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="d-grid gap-2">
                            <button class="btn btn-outline-primary" onclick="openProcess('${processId}')">
                                <i class="material-icons me-2">visibility</i>View Details
                            </button>
                            <button class="btn btn-outline-success" onclick="createWorkflow('${processId}')">
                                <i class="material-icons me-2">work</i>Create Workflow
                            </button>
                            <button class="btn btn-outline-warning" onclick="executeProcess('${processId}')">
                                <i class="material-icons me-2">play_arrow</i>Execute Process
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `);

    $('body').append(modal);
    const modalInstance = new bootstrap.Modal(modal[0]);
    modalInstance.show();

    modal.on('hidden.bs.modal', function() {
        modal.remove();
    });
}

function showCreateOptions() {
    const modal = $(`
        <div class="modal fade" id="createOptionsModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Create New</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="d-grid gap-2">
                            <a href="/processes/create" class="btn btn-primary">
                                <i class="material-icons me-2">business</i>Create Process
                                <br><small>Upload BPMN file and configure process</small>
                            </a>
                            <a href="/workflows/create" class="btn btn-success">
                                <i class="material-icons me-2">work</i>Create Workflow
                                <br><small>Build workflow from existing process</small>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `);

    $('body').append(modal);
    const modalInstance = new bootstrap.Modal(modal[0]);
    modalInstance.show();

    modal.on('hidden.bs.modal', function() {
        modal.remove();
    });
}

function createWorkflow(processId) {
    window.location.href = `/workflows/create?processId=${processId}`;
}

function executeProcess(processId) {
    // For demo purposes, navigate to a mock execution
    window.location.href = `/executions/monitor?processId=${processId}&executionId=exec_${processId}_${Date.now()}`;
}

// Export for global use
window.ExecutionMonitor = ExecutionMonitor;
window.formatDateTime = formatDateTime;
window.showLoading = showLoading;
window.hideLoading = hideLoading;
window.initializeHomePage = initializeHomePage;
window.filterProcesses = filterProcesses;
window.setFilter = setFilter;
window.refreshProcesses = refreshProcesses;
window.openProcess = openProcess;
window.showProcessMenu = showProcessMenu;
window.showCreateOptions = showCreateOptions;