/**
 * Custom properties provider for BPMN FormKey
 * Uses standard @bpmn-io/properties-panel components for v5.x compatibility
 */

import { SelectEntry } from '@bpmn-io/properties-panel';
import { useService } from 'bpmn-js-properties-panel';
import { useEffect, useState } from 'preact/hooks';

console.log('[FormKeyPropertiesProvider] Module Loaded (File Evaluated)');

// Module definition
export default {
  __init__: ['formKeyPropertiesProvider'],
  formKeyPropertiesProvider: ['type', FormKeyPropertiesProvider]
};

// Provider Constructor
function FormKeyPropertiesProvider(propertiesPanel, translate) {
  console.log('[FormKey] Constructor called. Registering provider...');

  this._translate = translate;

  // Define getGroups functionality
  this.getGroups = function (element) {
    return function (groups) {
      // Trace log
      console.log('[FormKey] getGroups called for:', element.type, element.id);

      // Only add to User Tasks
      if (element.type !== 'bpmn:UserTask') {
        return groups;
      }

      console.log('[FormKey] Adding custom group for User Task');

      // Add custom group at the top
      groups.unshift({
        id: 'custom-form-key-group',
        label: translate('Custom Form Selection'),
        entries: [
          {
            id: 'form-key-select',
            element,
            component: FormProps, // Preact Component
            isEdited: () => false
          }
        ]
      });

      return groups;
    };
  };

  // Register provider
  propertiesPanel.registerProvider(500, this);
  console.log('[FormKey] Provider registered.');
}

FormKeyPropertiesProvider.$inject = ['propertiesPanel', 'translate'];

// Preact Component for the Entry
function FormProps(props) {
  const { element, id } = props;
  console.log('[FormKey] FormProps rendering...');

  const modeling = useService('modeling');
  const translate = useService('translate');
  const debounce = useService('debounceInput');

  // State
  const [options, setOptions] = useState([
    { value: '', label: translate('Loading forms...') }
  ]);

  // Fetch Forms
  useEffect(() => {
    async function loadForms() {
      try {
        console.log('[FormKey] Fetching forms...');
        // Use window.location logic for robustness or configured API URL
        const API_URL = 'http://localhost:8084/api/v1/forms?size=100';

        const res = await fetch(API_URL);
        if (!res.ok) throw new Error(res.statusText);

        const data = await res.json();
        const list = data.content || data || [];

        const formOptions = list.map(f => ({
          value: f.key,
          label: `${f.name} (${f.key})`
        }));

        // Add default empty option
        formOptions.unshift({ value: '', label: translate('-- Select Form --') });

        setOptions(formOptions);
        console.log('[FormKey] Loaded', list.length, 'forms');

      } catch (err) {
        console.error('[FormKey] Failed to load forms', err);
        setOptions([
          { value: '', label: translate('Error: Could not load forms') }
        ]);
      }
    }
    loadForms();
  }, [translate]); // Dependencies

  const getValue = () => {
    return element.businessObject.get('camunda:formKey') || '';
  };

  const setValue = (value) => {
    return modeling.updateProperties(element, {
      'camunda:formKey': value || undefined
    });
  };

  return SelectEntry({
    id,
    element,
    label: translate('Form Key'),
    getValue,
    setValue,
    getOptions: () => options,
    debounce
  });
}
