declare module 'bpmn-js/lib/Modeler' {
    export default class BpmnModeler {
        constructor(options?: { container?: HTMLElement; keyboard?: { bindTo?: Window | Element } });
        importXML(xml: string): Promise<{ warnings: string[] }>;
        saveXML(options?: { format?: boolean }): Promise<{ xml?: string }>;
        get(name: string): unknown;
        on(event: string, callback: () => void): void;
        destroy(): void;
    }
}

declare module 'dmn-js/lib/Modeler' {
    export default class DmnModeler {
        constructor(options?: { container?: HTMLElement });
        importXML(xml: string): Promise<{ warnings: string[] }>;
        saveXML(options?: { format?: boolean }): Promise<{ xml?: string }>;
        get(name: string): unknown;
        on(event: string, callback: () => void): void;
        destroy(): void;
    }
}
