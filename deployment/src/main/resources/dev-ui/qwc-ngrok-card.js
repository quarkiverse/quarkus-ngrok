import { LitElement, html, css} from 'lit';
import { pages } from 'build-time-data';
import 'qwc/qwc-extension-link.js';

export class QwcNgrokCard extends LitElement {

    static styles = css`
      .identity {
        display: flex;
        justify-content: flex-start;
      }

      .description {
        padding-bottom: 10px;
      }

      .logo {
        padding-bottom: 10px;
        margin-right: 5px;
      }

      .card-content {
        color: var(--lumo-contrast-90pct);
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        padding: 2px 2px;
        height: 100%;
      }

      .card-content slot {
        display: flex;
        flex-flow: column wrap;
        padding-top: 5px;
      }
    `;

    static properties = {
        extensionName: {attribute: true},
        description: {attribute: true},
        guide: {attribute: true},
        namespace: {attribute: true},
    };

    constructor() {
        super();
    }

    connectedCallback() {
        super.connectedCallback();
    }

    render() {
        return html`<div class="card-content" slot="content">
            <div class="identity">
                <div class="logo">
                    <img src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48c3ZnIGlkPSJMYXllcl8xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MDAgNDAwIj48ZGVmcz48c3R5bGU+LmNscy0xe2ZpbGw6I2ZmZjt9LmNscy0ye2ZpbGw6IzEwMjM5ZTt9PC9zdHlsZT48L2RlZnM+PHJlY3QgY2xhc3M9ImNscy0yIiB3aWR0aD0iNDAwIiBoZWlnaHQ9IjQwMCIvPjxwYXRoIGNsYXNzPSJjbHMtMSIgZD0iTTMyNC4wNCw5NC45OWMtNS4wNy02LjExLTEwLjgtMTEuMzgtMTYuOTMtMTYuMDctNS40OC00LjEtMTEuMjktNy43LTE3LjU4LTEwLjYzLTMuMDMtMS40Mi02LjIyLTIuNi05LjU3LTMuNjgtNC44My0xLjU5LTEwLjA2LTIuNjgtMTUuNDYtMy42aC02OC4wNWwtNDQuOTksNTIuNDlWNjIuMDlINTZWMzM5aDk1LjQ2VjE1Mi4xNmg4OS42NWw3LjQ0LS4xN3YxODYuOTJoOTUuNDZWMTY1LjcyYzAtMTQuNzMtMS4zOS0yNy44OC00LjE3LTM5LjQzLTIuNzgtMTEuNDctOC4wMi0yMS44NS0xNS43OS0zMS4zMVoiLz48L3N2Zz4="
                                       alt="${this.extensionName}" 
                                       title="${this.extensionName}"
                                       width="32" 
                                       height="32">
                </div>
                <div class="description">${this.description}</div>
            </div>
            ${this._renderCardLinks()}
        </div>
        `;
    }

    _renderCardLinks(){
        return html`${pages.map(page => html`
                            <qwc-extension-link slot="link"
                                namespace="${this.namespace}"
                                extensionName="${this.extensionName}"
                                iconName="${page.icon}"
                                displayName="${page.title}"
                                staticLabel="${page.staticLabel}"
                                dynamicLabel="${page.dynamicLabel}"
                                streamingLabel="${page.streamingLabel}"
                                path="${page.id}"
                                ?embed=${page.embed}
                                externalUrl="${page.metadata.externalUrl}"
                                webcomponent="${page.componentLink}" >
                            </qwc-extension-link>
                        `)}`;
    }

}
customElements.define('qwc-ngrok-card', QwcNgrokCard);