/**
 * Custom properties provider for BPMN FormKey
 * Uses standard @bpmn-io/properties-panel components for v5.x compatibility
 */

import { SelectEntry } from '@bpmn-io/properties-panel';
import { useService } from 'bpmn-js-properties-panel';
import { useEffect, useState } from 'preact/hooks';

// Module definition
export default {
  __init__: ['formKeyPropertiesProvider'],
  formKeyPropertiesProvider: ['type', FormKeyPropertiesProvider]
};

// Provider Constructor
function FormKeyPropertiesProvider(propertiesPanel, translate) {
  propertiesPanel.registerProvider(500, this);

  this.getGroups = function (element) {
    return function (groups) {
      if (element.type !== 'bpmn:UserTask') {
        return groups;
      }

      groups.unshift({
        id: 'custom-form-key-group',
        label: translate('Custom Form Selection'),
        entries: [
          {
            id: 'form-key-select',
            element,
            component: FormProps, // Use our wrapper component
            isEdited: () => false
          }
        ]
      });

      return groups;
    };
  };
}

FormKeyPropertiesProvider.$inject = ['propertiesPanel', 'translate'];

// Component
function FormProps(props) {
  const { element, id } = props;

  const modeling = useService('modeling');
  const translate = useService('translate');
  const debounce = useService('debounceInput');

  const [options, setOptions] = useState([
    { value: '', label: translate('-- Loading Forms --') }
  ]);

  // Fetch Forms
  useEffect(() => {
    async function loadForms() {
      try {
        console.log('[FormKey] Fetching forms...');
        const res = await fetch('http://localhost:8084/api/v1/forms?size=100');
        if (!res.ok) throw new Error(res.statusText);

        const data = await res.json();
        const list = data.content || data || [];

        const formOptions = list.map(f => ({
          value: f.key,
          label: `${f.name} (${f.key})`
        }));

        formOptions.unshift({ value: '', label: translate('-- Select a Form --') });
        setOptions(formOptions);
        console.log('[FormKey] Loaded', list.length, 'forms');

      } catch (err) {
        console.error('[FormKey] Failed to load forms', err);
        setOptions([
          { value: '', label: translate('Error loading forms') }
        ]);
      }
    }
    loadForms();
  }, [translate]);

  const getValue = () => {
    return element.businessObject.get('camunda:formKey') || '';
  };

  const setValue = (value) => {
    return modeling.updateProperties(element, {
      'camunda:formKey': value || undefined
    });
  };

  // Render standard SelectEntry
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
