'use strict';


customElements.define('compodoc-menu', class extends HTMLElement {
    constructor() {
        super();
        this.isNormalMode = this.getAttribute('mode') === 'normal';
    }

    connectedCallback() {
        this.render(this.isNormalMode);
    }

    render(isNormalMode) {
        let tp = lithtml.html(`
        <nav>
            <ul class="list">
                <li class="title">
                    <a href="index.html" data-type="index-link">spring-petclinic-angular documentation</a>
                </li>

                <li class="divider"></li>
                ${ isNormalMode ? `<div id="book-search-input" role="search"><input type="text" placeholder="Type to search"></div>` : '' }
                <li class="chapter">
                    <a data-type="chapter-link" href="index.html"><span class="icon ion-ios-home"></span>Getting started</a>
                    <ul class="links">
                        <li class="link">
                            <a href="overview.html" data-type="chapter-link">
                                <span class="icon ion-ios-keypad"></span>Overview
                            </a>
                        </li>
                        <li class="link">
                            <a href="index.html" data-type="chapter-link">
                                <span class="icon ion-ios-paper"></span>README
                            </a>
                        </li>
                                <li class="link">
                                    <a href="dependencies.html" data-type="chapter-link">
                                        <span class="icon ion-ios-list"></span>Dependencies
                                    </a>
                                </li>
                    </ul>
                </li>
                    <li class="chapter modules">
                        <a data-type="chapter-link" href="modules.html">
                            <div class="menu-toggler linked" data-toggle="collapse" ${ isNormalMode ?
                                'data-target="#modules-links"' : 'data-target="#xs-modules-links"' }>
                                <span class="icon ion-ios-archive"></span>
                                <span class="link-name">Modules</span>
                                <span class="icon ion-ios-arrow-down"></span>
                            </div>
                        </a>
                        <ul class="links collapse " ${ isNormalMode ? 'id="modules-links"' : 'id="xs-modules-links"' }>
                            <li class="link">
                                <a href="modules/AppModule.html" data-type="entity-link">AppModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-AppModule-bf6538729326e80fe1d114e8509a0891"' : 'data-target="#xs-components-links-module-AppModule-bf6538729326e80fe1d114e8509a0891"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-AppModule-bf6538729326e80fe1d114e8509a0891"' :
                                            'id="xs-components-links-module-AppModule-bf6538729326e80fe1d114e8509a0891"' }>
                                            <li class="link">
                                                <a href="components/AppComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">AppComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                                <li class="chapter inner">
                                    <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                        'data-target="#injectables-links-module-AppModule-bf6538729326e80fe1d114e8509a0891"' : 'data-target="#xs-injectables-links-module-AppModule-bf6538729326e80fe1d114e8509a0891"' }>
                                        <span class="icon ion-md-arrow-round-down"></span>
                                        <span>Injectables</span>
                                        <span class="icon ion-ios-arrow-down"></span>
                                    </div>
                                    <ul class="links collapse" ${ isNormalMode ? 'id="injectables-links-module-AppModule-bf6538729326e80fe1d114e8509a0891"' :
                                        'id="xs-injectables-links-module-AppModule-bf6538729326e80fe1d114e8509a0891"' }>
                                        <li class="link">
                                            <a href="injectables/HttpErrorHandler.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>HttpErrorHandler</a>
                                        </li>
                                    </ul>
                                </li>
                            </li>
                            <li class="link">
                                <a href="modules/AppRoutingModule.html" data-type="entity-link">AppRoutingModule</a>
                            </li>
                            <li class="link">
                                <a href="modules/OwnersModule.html" data-type="entity-link">OwnersModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-OwnersModule-8bdf5589d2033b7a1035270d20f26b58"' : 'data-target="#xs-components-links-module-OwnersModule-8bdf5589d2033b7a1035270d20f26b58"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-OwnersModule-8bdf5589d2033b7a1035270d20f26b58"' :
                                            'id="xs-components-links-module-OwnersModule-8bdf5589d2033b7a1035270d20f26b58"' }>
                                            <li class="link">
                                                <a href="components/OwnerAddComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">OwnerAddComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/OwnerDetailComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">OwnerDetailComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/OwnerEditComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">OwnerEditComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/OwnerListComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">OwnerListComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                                <li class="chapter inner">
                                    <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                        'data-target="#injectables-links-module-OwnersModule-8bdf5589d2033b7a1035270d20f26b58"' : 'data-target="#xs-injectables-links-module-OwnersModule-8bdf5589d2033b7a1035270d20f26b58"' }>
                                        <span class="icon ion-md-arrow-round-down"></span>
                                        <span>Injectables</span>
                                        <span class="icon ion-ios-arrow-down"></span>
                                    </div>
                                    <ul class="links collapse" ${ isNormalMode ? 'id="injectables-links-module-OwnersModule-8bdf5589d2033b7a1035270d20f26b58"' :
                                        'id="xs-injectables-links-module-OwnersModule-8bdf5589d2033b7a1035270d20f26b58"' }>
                                        <li class="link">
                                            <a href="injectables/OwnerService.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>OwnerService</a>
                                        </li>
                                    </ul>
                                </li>
                            </li>
                            <li class="link">
                                <a href="modules/OwnersRoutingModule.html" data-type="entity-link">OwnersRoutingModule</a>
                            </li>
                            <li class="link">
                                <a href="modules/PartsModule.html" data-type="entity-link">PartsModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-PartsModule-7d449899ae32704450c06c34d2f0d346"' : 'data-target="#xs-components-links-module-PartsModule-7d449899ae32704450c06c34d2f0d346"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-PartsModule-7d449899ae32704450c06c34d2f0d346"' :
                                            'id="xs-components-links-module-PartsModule-7d449899ae32704450c06c34d2f0d346"' }>
                                            <li class="link">
                                                <a href="components/PageNotFoundComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">PageNotFoundComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/WelcomeComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">WelcomeComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                            </li>
                            <li class="link">
                                <a href="modules/PetsModule.html" data-type="entity-link">PetsModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-PetsModule-53d8972ae1274ba8e6727cdb2f242648"' : 'data-target="#xs-components-links-module-PetsModule-53d8972ae1274ba8e6727cdb2f242648"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-PetsModule-53d8972ae1274ba8e6727cdb2f242648"' :
                                            'id="xs-components-links-module-PetsModule-53d8972ae1274ba8e6727cdb2f242648"' }>
                                            <li class="link">
                                                <a href="components/PetAddComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">PetAddComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/PetEditComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">PetEditComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/PetListComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">PetListComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                                <li class="chapter inner">
                                    <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                        'data-target="#injectables-links-module-PetsModule-53d8972ae1274ba8e6727cdb2f242648"' : 'data-target="#xs-injectables-links-module-PetsModule-53d8972ae1274ba8e6727cdb2f242648"' }>
                                        <span class="icon ion-md-arrow-round-down"></span>
                                        <span>Injectables</span>
                                        <span class="icon ion-ios-arrow-down"></span>
                                    </div>
                                    <ul class="links collapse" ${ isNormalMode ? 'id="injectables-links-module-PetsModule-53d8972ae1274ba8e6727cdb2f242648"' :
                                        'id="xs-injectables-links-module-PetsModule-53d8972ae1274ba8e6727cdb2f242648"' }>
                                        <li class="link">
                                            <a href="injectables/PetService.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>PetService</a>
                                        </li>
                                    </ul>
                                </li>
                            </li>
                            <li class="link">
                                <a href="modules/PetsRoutingModule.html" data-type="entity-link">PetsRoutingModule</a>
                            </li>
                            <li class="link">
                                <a href="modules/PetTypesModule.html" data-type="entity-link">PetTypesModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-PetTypesModule-d1d33565fb7bf22f393bf0178bee2b99"' : 'data-target="#xs-components-links-module-PetTypesModule-d1d33565fb7bf22f393bf0178bee2b99"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-PetTypesModule-d1d33565fb7bf22f393bf0178bee2b99"' :
                                            'id="xs-components-links-module-PetTypesModule-d1d33565fb7bf22f393bf0178bee2b99"' }>
                                            <li class="link">
                                                <a href="components/PettypeAddComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">PettypeAddComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/PettypeEditComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">PettypeEditComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/PettypeListComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">PettypeListComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                                <li class="chapter inner">
                                    <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                        'data-target="#injectables-links-module-PetTypesModule-d1d33565fb7bf22f393bf0178bee2b99"' : 'data-target="#xs-injectables-links-module-PetTypesModule-d1d33565fb7bf22f393bf0178bee2b99"' }>
                                        <span class="icon ion-md-arrow-round-down"></span>
                                        <span>Injectables</span>
                                        <span class="icon ion-ios-arrow-down"></span>
                                    </div>
                                    <ul class="links collapse" ${ isNormalMode ? 'id="injectables-links-module-PetTypesModule-d1d33565fb7bf22f393bf0178bee2b99"' :
                                        'id="xs-injectables-links-module-PetTypesModule-d1d33565fb7bf22f393bf0178bee2b99"' }>
                                        <li class="link">
                                            <a href="injectables/PetTypeService.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>PetTypeService</a>
                                        </li>
                                    </ul>
                                </li>
                            </li>
                            <li class="link">
                                <a href="modules/PettypesRoutingModule.html" data-type="entity-link">PettypesRoutingModule</a>
                            </li>
                            <li class="link">
                                <a href="modules/SpecialtiesModule.html" data-type="entity-link">SpecialtiesModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-SpecialtiesModule-b15b7a96b130f96737eb76e0e5d80fa2"' : 'data-target="#xs-components-links-module-SpecialtiesModule-b15b7a96b130f96737eb76e0e5d80fa2"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-SpecialtiesModule-b15b7a96b130f96737eb76e0e5d80fa2"' :
                                            'id="xs-components-links-module-SpecialtiesModule-b15b7a96b130f96737eb76e0e5d80fa2"' }>
                                            <li class="link">
                                                <a href="components/SpecialtyAddComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">SpecialtyAddComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/SpecialtyEditComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">SpecialtyEditComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/SpecialtyListComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">SpecialtyListComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                                <li class="chapter inner">
                                    <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                        'data-target="#injectables-links-module-SpecialtiesModule-b15b7a96b130f96737eb76e0e5d80fa2"' : 'data-target="#xs-injectables-links-module-SpecialtiesModule-b15b7a96b130f96737eb76e0e5d80fa2"' }>
                                        <span class="icon ion-md-arrow-round-down"></span>
                                        <span>Injectables</span>
                                        <span class="icon ion-ios-arrow-down"></span>
                                    </div>
                                    <ul class="links collapse" ${ isNormalMode ? 'id="injectables-links-module-SpecialtiesModule-b15b7a96b130f96737eb76e0e5d80fa2"' :
                                        'id="xs-injectables-links-module-SpecialtiesModule-b15b7a96b130f96737eb76e0e5d80fa2"' }>
                                        <li class="link">
                                            <a href="injectables/SpecialtyService.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>SpecialtyService</a>
                                        </li>
                                    </ul>
                                </li>
                            </li>
                            <li class="link">
                                <a href="modules/SpecialtiesRoutingModule.html" data-type="entity-link">SpecialtiesRoutingModule</a>
                            </li>
                            <li class="link">
                                <a href="modules/TestingModule.html" data-type="entity-link">TestingModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-TestingModule-0bb8b4c7591b7b5aceca4884c65de29d"' : 'data-target="#xs-components-links-module-TestingModule-0bb8b4c7591b7b5aceca4884c65de29d"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-TestingModule-0bb8b4c7591b7b5aceca4884c65de29d"' :
                                            'id="xs-components-links-module-TestingModule-0bb8b4c7591b7b5aceca4884c65de29d"' }>
                                            <li class="link">
                                                <a href="components/DummyComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">DummyComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/RouterOutletStubComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">RouterOutletStubComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                                <li class="chapter inner">
                                    <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                        'data-target="#directives-links-module-TestingModule-0bb8b4c7591b7b5aceca4884c65de29d"' : 'data-target="#xs-directives-links-module-TestingModule-0bb8b4c7591b7b5aceca4884c65de29d"' }>
                                        <span class="icon ion-md-code-working"></span>
                                        <span>Directives</span>
                                        <span class="icon ion-ios-arrow-down"></span>
                                    </div>
                                    <ul class="links collapse" ${ isNormalMode ? 'id="directives-links-module-TestingModule-0bb8b4c7591b7b5aceca4884c65de29d"' :
                                        'id="xs-directives-links-module-TestingModule-0bb8b4c7591b7b5aceca4884c65de29d"' }>
                                        <li class="link">
                                            <a href="directives/RouterLinkStubDirective.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules">RouterLinkStubDirective</a>
                                        </li>
                                    </ul>
                                </li>
                            </li>
                            <li class="link">
                                <a href="modules/VetsModule.html" data-type="entity-link">VetsModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-VetsModule-dd53475ccbeba2329b0972a1537649dd"' : 'data-target="#xs-components-links-module-VetsModule-dd53475ccbeba2329b0972a1537649dd"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-VetsModule-dd53475ccbeba2329b0972a1537649dd"' :
                                            'id="xs-components-links-module-VetsModule-dd53475ccbeba2329b0972a1537649dd"' }>
                                            <li class="link">
                                                <a href="components/VetAddComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">VetAddComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/VetEditComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">VetEditComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/VetListComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">VetListComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                                <li class="chapter inner">
                                    <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                        'data-target="#injectables-links-module-VetsModule-dd53475ccbeba2329b0972a1537649dd"' : 'data-target="#xs-injectables-links-module-VetsModule-dd53475ccbeba2329b0972a1537649dd"' }>
                                        <span class="icon ion-md-arrow-round-down"></span>
                                        <span>Injectables</span>
                                        <span class="icon ion-ios-arrow-down"></span>
                                    </div>
                                    <ul class="links collapse" ${ isNormalMode ? 'id="injectables-links-module-VetsModule-dd53475ccbeba2329b0972a1537649dd"' :
                                        'id="xs-injectables-links-module-VetsModule-dd53475ccbeba2329b0972a1537649dd"' }>
                                        <li class="link">
                                            <a href="injectables/VetService.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>VetService</a>
                                        </li>
                                    </ul>
                                </li>
                            </li>
                            <li class="link">
                                <a href="modules/VetsRoutingModule.html" data-type="entity-link">VetsRoutingModule</a>
                            </li>
                            <li class="link">
                                <a href="modules/VisitsModule.html" data-type="entity-link">VisitsModule</a>
                                    <li class="chapter inner">
                                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                            'data-target="#components-links-module-VisitsModule-f01d2ef6b4e454c8e1b860fd175f4b02"' : 'data-target="#xs-components-links-module-VisitsModule-f01d2ef6b4e454c8e1b860fd175f4b02"' }>
                                            <span class="icon ion-md-cog"></span>
                                            <span>Components</span>
                                            <span class="icon ion-ios-arrow-down"></span>
                                        </div>
                                        <ul class="links collapse" ${ isNormalMode ? 'id="components-links-module-VisitsModule-f01d2ef6b4e454c8e1b860fd175f4b02"' :
                                            'id="xs-components-links-module-VisitsModule-f01d2ef6b4e454c8e1b860fd175f4b02"' }>
                                            <li class="link">
                                                <a href="components/VisitAddComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">VisitAddComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/VisitEditComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">VisitEditComponent</a>
                                            </li>
                                            <li class="link">
                                                <a href="components/VisitListComponent.html"
                                                    data-type="entity-link" data-context="sub-entity" data-context-id="modules">VisitListComponent</a>
                                            </li>
                                        </ul>
                                    </li>
                                <li class="chapter inner">
                                    <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ?
                                        'data-target="#injectables-links-module-VisitsModule-f01d2ef6b4e454c8e1b860fd175f4b02"' : 'data-target="#xs-injectables-links-module-VisitsModule-f01d2ef6b4e454c8e1b860fd175f4b02"' }>
                                        <span class="icon ion-md-arrow-round-down"></span>
                                        <span>Injectables</span>
                                        <span class="icon ion-ios-arrow-down"></span>
                                    </div>
                                    <ul class="links collapse" ${ isNormalMode ? 'id="injectables-links-module-VisitsModule-f01d2ef6b4e454c8e1b860fd175f4b02"' :
                                        'id="xs-injectables-links-module-VisitsModule-f01d2ef6b4e454c8e1b860fd175f4b02"' }>
                                        <li class="link">
                                            <a href="injectables/VisitService.html"
                                                data-type="entity-link" data-context="sub-entity" data-context-id="modules" }>VisitService</a>
                                        </li>
                                    </ul>
                                </li>
                            </li>
                            <li class="link">
                                <a href="modules/VisitsRoutingModule.html" data-type="entity-link">VisitsRoutingModule</a>
                            </li>
                </ul>
                </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ? 'data-target="#guards-links"' :
                            'data-target="#xs-guards-links"' }>
                            <span class="icon ion-ios-lock"></span>
                            <span>Guards</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="guards-links"' : 'id="xs-guards-links"' }>
                            <li class="link">
                                <a href="guards/SpecResolver.html" data-type="entity-link">SpecResolver</a>
                            </li>
                            <li class="link">
                                <a href="guards/VetResolver.html" data-type="entity-link">VetResolver</a>
                            </li>
                        </ul>
                    </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ? 'data-target="#interfaces-links"' :
                            'data-target="#xs-interfaces-links"' }>
                            <span class="icon ion-md-information-circle-outline"></span>
                            <span>Interfaces</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? ' id="interfaces-links"' : 'id="xs-interfaces-links"' }>
                            <li class="link">
                                <a href="interfaces/Owner.html" data-type="entity-link">Owner</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/Pet.html" data-type="entity-link">Pet</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/PetType.html" data-type="entity-link">PetType</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/Specialty.html" data-type="entity-link">Specialty</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/Vet.html" data-type="entity-link">Vet</a>
                            </li>
                            <li class="link">
                                <a href="interfaces/Visit.html" data-type="entity-link">Visit</a>
                            </li>
                        </ul>
                    </li>
                    <li class="chapter">
                        <div class="simple menu-toggler" data-toggle="collapse" ${ isNormalMode ? 'data-target="#miscellaneous-links"'
                            : 'data-target="#xs-miscellaneous-links"' }>
                            <span class="icon ion-ios-cube"></span>
                            <span>Miscellaneous</span>
                            <span class="icon ion-ios-arrow-down"></span>
                        </div>
                        <ul class="links collapse " ${ isNormalMode ? 'id="miscellaneous-links"' : 'id="xs-miscellaneous-links"' }>
                            <li class="link">
                                <a href="miscellaneous/typealiases.html" data-type="entity-link">Type aliases</a>
                            </li>
                            <li class="link">
                                <a href="miscellaneous/variables.html" data-type="entity-link">Variables</a>
                            </li>
                        </ul>
                    </li>
                        <li class="chapter">
                            <a data-type="chapter-link" href="routes.html"><span class="icon ion-ios-git-branch"></span>Routes</a>
                        </li>
                    <li class="chapter">
                        <a data-type="chapter-link" href="coverage.html"><span class="icon ion-ios-stats"></span>Documentation coverage</a>
                    </li>
                    <li class="divider"></li>
                    <li class="copyright">
                        Documentation generated using <a href="https://compodoc.app/" target="_blank">
                            <img data-src="images/compodoc-vectorise.png" class="img-responsive" data-type="compodoc-logo">
                        </a>
                    </li>
            </ul>
        </nav>
        `);
        this.innerHTML = tp.strings;
    }
});