package com.example.cicsgenapp.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * BreadcrumbNavigation component for displaying navigation breadcrumbs.
 *
 * <p>Provides a simple breadcrumb trail showing the current page location and allowing
 * navigation to parent pages.
 */
public class BreadcrumbNavigation extends HorizontalLayout {

    private List<Item> items = new ArrayList<>();

    public BreadcrumbNavigation() {
        setSpacing(false);
        addClassNames(LumoUtility.Padding.Bottom.MEDIUM, LumoUtility.Gap.SMALL);
    }

    public void setItems(Item... breadcrumbItems) {
        removeAll();
        items = List.of(breadcrumbItems);

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            if (i > 0) {
                // Add separator
                Span separator = new Span(">");
                separator.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Padding.Horizontal.SMALL);
                add(separator);
            }

            if (item.getRoute() == null) {
                // Last item (current page) - not a link
                Span span = new Span(item.getLabel());
                span.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontWeight.MEDIUM);
                add(span);
            } else {
                // Clickable breadcrumb
                Button button = new Button(item.getLabel());
                button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                button.addClassNames(LumoUtility.TextColor.PRIMARY);
                button.addClickListener(event ->
                        getUI().ifPresent(ui -> ui.navigate(item.getRoute()))
                );
                add(button);
            }
        }
    }

    /**
     * Breadcrumb item data class.
     */
    public static class Item {
        private final String label;
        private final String route; // null for current page (non-clickable)

        public Item(String label, String route) {
            this.label = label;
            this.route = route;
        }

        public String getLabel() {
            return label;
        }

        public String getRoute() {
            return route;
        }
    }
}
