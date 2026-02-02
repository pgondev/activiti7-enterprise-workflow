/**
 * Custom properties provider for BPMN that adds a form selector dropdown.
 * This replaces the default text input with a dropdown showing available forms.
 */

import { html } from 'htm/preact'
import { useEffect, useState } from 'preact/hooks'

const FORM_KEY_PROPERTY = 'camunda:formKey'

export default class FormKeyPropertiesProvider {
    constructor(propertiesPanel, injector, translate, modeling, bpmnFactory) {
        this._propertiesPanel = propertiesPanel
        this._injector = injector
        this._translate = translate
        this._modeling = modeling
        this._bpmnFactory = bpmnFactory

        // Register with properties panel
        propertiesPanel.registerProvider(500, this)
    }

    /**
     * Get groups (sections) for the properties panel
     */
    getGroups(element) {
        return (groups) => {
            // Only add for user tasks and start events
            if (!this._isUserTask(element) && !this._isStartEvent(element)) {
                return groups
            }

            // Find or create the forms group
            const formsGroupIndex = groups.findIndex(g => g.id === 'forms')

            if (formsGroupIndex !== -1) {
                // Replace existing forms group entries with our custom dropdown
                const formsGroup = groups[formsGroupIndex]
                formsGroup.entries = [
                    ...formsGroup.entries,
                    {
                        id: 'formKeySelector',
                        element,
                        component: FormKeySelector,
                        isEdited: () => false
                    }
                ]
            } else {
                // Add new forms group if it doesn't exist
                groups.push({
                    id: 'formKey',
                    label: this._translate('Form Selection'),
                    entries: [{
                        id: 'formKeySelector',
                        element,
                        component: FormKeySelector,
                        isEdited: () => false
                    }]
                })
            }

            return groups
        }
    }

    _isUserTask(element) {
        return element.type === 'bpmn:UserTask'
    }

    _isStartEvent(element) {
        return element.type === 'bpmn:StartEvent'
    }
}

FormKeyPropertiesProvider.$inject = [
    'propertiesPanel',
    'injector',
    'translate',
    'modeling',
    'bpmnFactory'
]

/**
 * Form Key Selector Component - Dropdown to select forms
 */
function FormKeySelector(props) {
    const { element, injector } = props
    const modeling = injector.get('modeling')
    const [forms, setForms] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    // Get current formKey value from element
    const businessObject = element.businessObject
    const currentFormKey = businessObject.get(FORM_KEY_PROPERTY) || ''

    useEffect(() => {
        // Fetch forms from API
        const fetchForms = async () => {
            try {
                setLoading(true)
                const response = await fetch('http://localhost:8084/api/v1/forms?size=100')

                if (!response.ok) {
                    throw new Error(`Failed to fetch forms: ${response.statusText}`)
                }

                const data = await response.json()
                // Handle both paginated and direct array responses
                const formsList = data.content || data
                setForms(formsList)
                setError(null)
            } catch (err) {
                console.error('Error fetching forms:', err)
                setError(err.message)
                setForms([])
            } finally {
                setLoading(false)
            }
        }

        fetchForms()
    }, [])

    const handleChange = (event) => {
        const newFormKey = event.target.value

        // Update the BPMN element with new formKey
        modeling.updateProperties(element, {
            [FORM_KEY_PROPERTY]: newFormKey || undefined
        })
    }

    if (loading) {
        return html`
      <div class="bio-properties-panel-entry">
        <label class="bio-properties-panel-label">Form Key</label>
        <div class="bio-properties-panel-field-wrapper">
          <div style="padding: 8px; color: #666;">Loading forms...</div>
        </div>
      </div>
    `
    }

    if (error) {
        return html`
      <div class="bio-properties-panel-entry">
        <label class="bio-properties-panel-label">Form Key</label>
        <div class="bio-properties-panel-field-wrapper">
          <input
            type="text"
            class="bio-properties-panel-input"
            value=${currentFormKey}
            onInput=${handleChange}
            placeholder="Enter form key manually (API error)"
          />
          <div style="font-size: 11px; color: #d32f2f; margin-top: 4px;">
            ${error}
          </div>
        </div>
      </div>
    `
    }

    return html`
    <div class="bio-properties-panel-entry">
      <label class="bio-properties-panel-label">
        Form Key
        ${currentFormKey && html`<span style="color: #4caf50; margin-left: 4px;">✓</span>`}
      </label>
      <div class="bio-properties-panel-field-wrapper">
        <select
          class="bio-properties-panel-input"
          value=${currentFormKey}
          onChange=${handleChange}
        >
          <option value="">-- Select Form --</option>
          ${forms.map(form => html`
            <option value=${form.key} selected=${form.key === currentFormKey}>
              ${form.name} (${form.key})
            </option>
          `)}
        </select>
        ${forms.length === 0 && html`
          <div style="font-size: 11px; color: #ff9800; margin-top: 4px;">
            No forms available. Create forms in the Forms UI first.
          </div>
        `}
      </div>
    </div>
  `
}
