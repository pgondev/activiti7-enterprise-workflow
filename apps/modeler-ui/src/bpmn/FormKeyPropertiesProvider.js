/**
 * Custom properties provider for BPMN FormKey
 * Uses Preact components for bpmn-js-properties-panel@5.x compatibility
 */

import { html } from 'htm/preact';
import { useState, useEffect } from 'preact/hooks';

// Constructor
function FormKeyPropertiesProvider(propertiesPanel, injector, translate) {
  this._injector = injector;
  this._translate = translate;

  // Register provider
  propertiesPanel.registerProvider(600, this);
}

FormKeyPropertiesProvider.$inject = [
  'propertiesPanel',
  'injector',
  'translate'
];

// Group definition
FormKeyPropertiesProvider.prototype.getGroups = function (element) {
  return (groups) => {
    // Only for User Tasks
    if (element.type !== 'bpmn:UserTask') {
      return groups;
    }

    // Add Custom Group
    groups.unshift({
      id: 'custom-form-key-group',
      label: this._translate('Custom Form Selection'),
      entries: [
        {
          id: 'custom-form-key-selector',
          element,
          component: FormKeySelector, // Use Preact component
          isEdited: () => false
        }
      ]
    });

    return groups;
  };
};

// Top-level Preact Component
function FormKeySelector(props) {
  const { element, injector } = props;
  const modeling = injector.get('modeling');
  const [forms, setForms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Get current value
  const businessObject = element.businessObject;
  const currentFormKey = businessObject.get('camunda:formKey') || '';

  // Fetch Forms Effect
  useEffect(() => {
    const API_URL = 'http://localhost:8084/api/v1/forms?size=100';
    console.log('[FormKeySelector] Fetching forms from', API_URL);

    fetch(API_URL)
      .then(res => {
        if (!res.ok) throw new Error(res.statusText);
        return res.json();
      })
      .then(data => {
        const list = data.content || data || [];
        setForms(list);
        setLoading(false);
      })
      .catch(err => {
        console.error('[FormKeySelector] Error:', err);
        setError(err.message);
        setLoading(false);
      });
  }, []);

  // Handle Change
  const handleChange = (e) => {
    const newValue = e.target.value;
    modeling.updateProperties(element, {
      'camunda:formKey': newValue || undefined
    });
  };

  // Render Loading
  if (loading) {
    return html`
      <div class="bio-properties-panel-entry">
        <label class="bio-properties-panel-label">Form Key</label>
        <div class="bio-properties-panel-field-wrapper">
          <div class="bio-properties-panel-input" style="color: #666; padding: 5px;">Loading forms...</div>
        </div>
      </div>
    `;
  }

  // Render Error
  if (error) {
    return html`
      <div class="bio-properties-panel-entry">
        <label class="bio-properties-panel-label">Form Key (Error)</label>
        <div class="bio-properties-panel-field-wrapper">
          <div style="color: red; font-size: 12px;">Failed to load: ${error}</div>
          <input 
            class="bio-properties-panel-input"
            value=${currentFormKey}
            onInput=${handleChange}
            placeholder="Type key manually"
          />
        </div>
      </div>
    `;
  }

  // Render Dropdown
  return html`
    <div class="bio-properties-panel-entry">
      <label class="bio-properties-panel-label">
        Select Form
        ${currentFormKey && html`<span style="margin-left: 5px; color: green; font-weight: bold;">✓</span>`}
      </label>
      <div class="bio-properties-panel-field-wrapper">
        <select class="bio-properties-panel-input" onChange=${handleChange} value=${currentFormKey}>
          <option value="">-- Choose a Form --</option>
          ${forms.map(form => html`
            <option value=${form.key}>${form.name} (${form.key})</option>
          `)}
        </select>
      </div>
       ${currentFormKey && html`
        <div class="bio-properties-panel-description">
          Current Key: <code>${currentFormKey}</code>
        </div>
      `}
    </div>
  `;
}

// Export as Module Definition (Critical Fix)
export default {
  __init__: ['formKeyPropertiesProvider'],
  formKeyPropertiesProvider: ['type', FormKeyPropertiesProvider]
};
