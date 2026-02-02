/**
 * Custom properties provider for BPMN FormKey
 * Adapted to be compatible with bpmn-js-properties-panel@5.x
 */

function FormKeyPropertiesProvider(
  propertiesPanel,
  translate,
  modeling,
  elementRegistry
) {
  // Register provider with lower priority
  propertiesPanel.registerProvider(600, this);

  this._translate = translate;
  this._modeling = modeling;
  this._elementRegistry = elementRegistry;

  // Store forms list
  this._forms = [];
  this._loading = true;

  console.log('[FormKeyPropertiesProvider] Initializing...');

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

  // Use relative URL or ensure CORS is handled
  const API_URL = 'http://localhost:8084/api/v1/forms?size=100';

  console.log('[FormKeyPropertiesProvider] Fetching forms from', API_URL);

  fetch(API_URL)
    .then(response => {
      if (!response.ok) {
        throw new Error('Failed to fetch forms: ' + response.statusText);
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
 * Get property parameters
 */
FormKeyPropertiesProvider.prototype.getTabs = function (element) {
  return function (tabs) {
    return tabs;
  };
};

/**
 * Get property groups
 */
FormKeyPropertiesProvider.prototype.getGroups = function (element) {
  const self = this;

  return function (groups) {
    // Only for User Tasks
    if (element.type !== 'bpmn:UserTask') {
      return groups;
    }

    console.log('[FormKeyPropertiesProvider] Creating group for', element.id);

    // Create a NEW group specifically for our dropdown to avoid conflicts
    const customGroup = {
      id: 'custom-form-key-group',
      label: self._translate('Custom Form Selection'),
      entries: []
    };

    // Add dropdown entry
    customGroup.entries.push({
      id: 'custom-form-key-selector',
      label: self._translate('Select Form'),
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
        const currentFormKey = element.businessObject.get('camunda:formKey') || '';

        let html = '<div class="bio-properties-panel-entry bio-properties-panel-select">';
        html += '<label class="bio-properties-panel-label" for="custom-form-key-select">Available Forms</label>';
        html += '<div class="bio-properties-panel-field-wrapper">';
        html += '<select id="custom-form-key-select" name="formKey" class="bio-properties-panel-input">';
        html += '<option value="">-- Choose a Form --</option>';

        if (self._loading) {
          html += '<option disabled>Loading forms...</option>';
        } else if (self._forms.length === 0) {
          html += '<option disabled>No forms available (API)</option>';
        } else {
          self._forms.forEach(function (form) {
            const selected = form.key === currentFormKey ? ' selected' : '';
            html += '<option value="' + escapeHtml(form.key) + '"' + selected + '>';
            html += escapeHtml(form.name) + '</option>';
          });
        }

        html += '</select>';
        html += '</div>';

        if (currentFormKey) {
          html += '<div class="bio-properties-panel-description">Current Key: <code>' + escapeHtml(currentFormKey) + '</code></div>';
        }
        html += '</div>';

        // Attach listener
        setTimeout(() => {
          const select = node.querySelector('#custom-form-key-select');
          if (select && !select._bound) {
            select._bound = true;
            select.addEventListener('change', function (e) {
              const newValue = e.target.value;
              self._modeling.updateProperties(element, {
                'camunda:formKey': newValue || undefined
              });
            });
          }
        }, 0);

        return html;
      }
    });

    // Add our group to the top
    groups.unshift(customGroup);

    return groups;
  };
};

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

// Export as a module definition for bpmn-js
export default {
  __init__: ['formKeyPropertiesProvider'],
  formKeyPropertiesProvider: ['type', FormKeyPropertiesProvider]
};
