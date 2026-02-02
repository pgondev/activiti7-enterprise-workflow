/**
 * Custom properties provider for BPMN FormKey
 * Uses standard @bpmn-io/properties-panel components for v5.x compatibility
 */

import { SelectEntry } from '@bpmn-io/properties-panel';
import { useService } from 'bpmn-js-properties-panel';
import { useEffect, useState } from 'preact/hooks';

// Provider Class
class FormKeyPropertiesProvider {
  constructor(propertiesPanel, translate) {
    this._translate = translate;

    // Register provider
    propertiesPanel.registerProvider(500, this);
  }

  /**
   * Return the groups function for the given element
   * @param {ModdleElement} element
   * @return {(groups: any[]) => any[]}
   */
  getGroups(element) {
    return (groups) => {
      // Only add to User Tasks
      if (element.type !== 'bpmn:UserTask') {
        return groups;
      }

      // Add custom group at the top
      groups.unshift({
        id: 'custom-form-key-group',
        label: this._translate('Custom Form Selection'),
        entries: [
          {
            id: 'form-key-select',
            element,
            component: FormProps, // Component handling the logic
            isEdited: () => false
          }
        ]
      });

      return groups;
    };
  }
}

FormKeyPropertiesProvider.$inject = ['propertiesPanel', 'translate'];

// Preact Component for the Entry
function FormProps(props) {
  const { element, id } = props;

  const modeling = useService('modeling');
  const translate = useService('translate');
  const debounce = useService('debounceInput');

  // State
  const [options, setOptions] = useState([
    { value: '', label: translate('Loading...') }
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

// Module Definition
export default {
  __init__: ['formKeyPropertiesProvider'],
  formKeyPropertiesProvider: ['type', FormKeyPropertiesProvider]
};
