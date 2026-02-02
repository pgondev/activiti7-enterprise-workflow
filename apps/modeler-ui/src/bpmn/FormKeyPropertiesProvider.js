/**
 * Custom properties provider for BPMN FormKey - Compatible with bpmn-js-properties-panel@5.x
 * This provider adds a form selector dropdown to replace the default formKey text input
 */

// Note: This uses the older properties-panel API for version 5.x
export default function FormKeyPropertiesProvider(
  propertiesPanel,
  translate,
  modeling,
  elementRegistry
) {
  // Register provider with lower priority so it runs after default providers
  propertiesPanel.registerProvider(600, this);

  this._translate = translate;
  this._modeling = modeling;
  this._elementRegistry = elementRegistry;

  // Store forms list
  this._forms = [];
  this._loading = true;

  // Fetch forms from API
  this._fetchForms();
}

FormKeyPropertiesProvider.$inject = [
  'propertiesPanel',
  'translate',
  'modeling',
  'elementRegistry'
];

/**
 * Fetch available forms from form-service API
 */
FormKeyPropertiesProvider.prototype._fetchForms = function () {
  const self = this;

  fetch('http://localhost:8084/api/v1/forms?size=100')
    .then(response => {
      if (!response.ok) {
        throw new Error('Failed to fetch forms');
      }
      return response.json();
    })
    .then(data => {
      self._forms = data.content || data || [];
      self._loading = false;
      console.log('[FormKeyPropertiesProvider] Loaded ' + self._forms.length + ' forms');
    })
    .catch(error => {
      console.error('[FormKeyPropertiesProvider] Error fetching forms:', error);
      self._forms = [];
      self._loading = false;
    });
};

/**
 * Get property tabs - not used in this version, but required by interface
 */
FormKeyPropertiesProvider.prototype.getTabs = function (element) {
  return function (tabs) {
    return tabs;
  };
};

/**
 * Get property groups - this is where we add our custom form selector
 */
FormKeyPropertiesProvider.prototype.getGroups = function (element) {
  const self = this;

  return function (groups) {
    // Only add for user tasks and start events
    if (!isUserTask(element) && !isStartEvent(element)) {
      return groups;
    }

    // Find the forms group
    let formsGroupIndex = -1;
    for (let i = 0; i < groups.length; i++) {
      if (groups[i].id === 'forms' || groups[i].id === 'CamundaPlatform__Forms') {
        formsGroupIndex = i;
        break;
      }
    }

    if (formsGroupIndex !== -1) {
      // Modify existing forms group to add our dropdown
      const formsGroup = groups[formsGroupIndex];

      // Add custom form selector entry at the beginning
      formsGroup.entries.unshift({
        id: 'form-key-selector',
        label: self._translate('Form Key (Dropdown)'),
        modelProperty: 'camunda:formKey',
        get: function (element) {
          const bo = element.businessObject;
          return {
            formKey: bo.get('camunda:formKey') || ''
          };
        },
        set: function (element, values) {
          return self._modeling.updateProperties(element, {
            'camunda:formKey': values.formKey || undefined
          });
        },
        html: function (element, node) {
          // Create dropdown HTML
          const currentFormKey = element.businessObject.get('camunda:formKey') || '';

          let html = '<div class="bpp-field-wrapper">';
          html += '<label for="camunda-form-key-select">Select Form</label>';
          html += '<select id="camunda-form-key-select" name="formKey" data-entry="form-key-selector">';
          html += '<option value="">-- Select Form --</option>';

          if (self._loading) {
            html += '<option disabled>Loading forms...</option>';
          } else if (self._forms.length === 0) {
            html += '<option disabled>No forms available</option>';
          } else {
            self._forms.forEach(function (form) {
              const selected = form.key === currentFormKey ? ' selected' : '';
              html += '<option value="' + escapeHtml(form.key) + '"' + selected + '>';
              html += escapeHtml(form.name) + ' (' + escapeHtml(form.key) + ')';
              html += '</option>';
            });
          }

          html += '</select>';

          if (currentFormKey) {
            html += '<div style="margin-top:4px;color:#4caf50;font-size:12px;">✓ Form assigned: ' + escapeHtml(currentFormKey) + '</div>';
          }

          html += '</div>';

          node.innerHTML = html;

          // Attach change event listener
          const select = node.querySelector('#camunda-form-key-select');
          if (select) {
            select.addEventListener('change', function (e) {
              const newValue = e.target.value;
              self._modeling.updateProperties(element, {
                'camunda:formKey': newValue || undefined
              });
            });
          }

          return node;
        }
      });
    }

    return groups;
  };
};

// Helper functions
function isUserTask(element) {
  return element.type === 'bpmn:UserTask';
}

function isStartEvent(element) {
  return element.type === 'bpmn:StartEvent';
}

function escapeHtml(text) {
  const map = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#039;'
  };
  return String(text).replace(/[&<>"']/g, function (m) { return map[m]; });
}
