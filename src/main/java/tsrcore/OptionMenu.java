package tsrcore;

import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import static mindustry.Vars.mods;

public class OptionMenu {
    String title;
    String description;
    HashMap<String, String> options;
    int maxItemsPerPage;
    Consumer<String> callback;
    int lastPage;
    int lastOffset;

    /**
     * Create an OptionMenu, which allows a player to select an option through a visual menu
     * @param title Title of the menu
     * @param description Description of the menu
     * @param options The buttons to show in the menu
     * @param callback Code to run with the result
     */
    public OptionMenu(String title, String description, HashMap<String, String> options, Consumer<String> callback) {
        this.title = title;
        this.description = description;
        this.options = options;
        this.maxItemsPerPage = 6;
        this.callback = callback;
    }

    /**
     * Create an OptionMenu, which allows a player to select an option through a visual menu
     * @param title Title of the menu
     * @param description Description of the menu
     * @param options The buttons to show in the menu
     * @param maxItemsPerPage Max items to show per page
     * @param callback Code to run with the result
     */
    public OptionMenu(String title, String description, HashMap<String, String> options, int maxItemsPerPage, Consumer<String> callback) {
        this.title = title;
        this.description = description;
        this.options = options;
        this.maxItemsPerPage = maxItemsPerPage;
        this.callback = callback;
        Call.label("Hi", 100, 100, 100);
    }

    /**
     * Open the menu for a player
     * @param player Player to open the menu for
     */
    public void open(Player player) {
        this.open(player, 0);
    }

    /**
     * Open the menu for a player, on a specific page
     * @param player The player to open the menu for
     * @param page The page to open it on
     */
    public void open(Player player, int page) {
        TSRCore tsrCore = (TSRCore) mods.list().find(m -> m.name.equals("tsrcore")).main;
        int menuId = tsrCore.registerMenu(this);
        ArrayList<ArrayList<String>> menu = new ArrayList<>();

        int pageCount = (int) Math.ceil((double) options.size() / maxItemsPerPage);
        int itemsOnPage = pageCount == page + 1 ? this.options.size() % maxItemsPerPage : maxItemsPerPage;
        if (itemsOnPage == 0) itemsOnPage = maxItemsPerPage;
        int offset = page * maxItemsPerPage;
        ArrayList<String> items = new ArrayList<>(this.options.keySet());

        this.lastPage = page;
        this.lastOffset = offset;

        for (int i = 0; i < itemsOnPage; i++) {
            String item = items.get(i + offset);
            ArrayList<String> row = new ArrayList<>();
            row.add(item);
            menu.add(row);
        }

        if (pageCount > 1) {
            ArrayList<String> row = new ArrayList<>();
            if (page > 0) row.add("[green]\uE825");
            else row.add("[red]\uE868");
            if (page + 1 < pageCount) row.add("[green]\uE83A");
            else row.add("[red]\uE868");
            menu.add(row);
        }

        List<String[]> array = new ArrayList<>();
        for (List<String> list : menu) {
            array.add(list.toArray(new String[0]));
        }

        Call.menu(player.con(), menuId, this.title, this.description, array.toArray(new String[0][0]));
    }

    /**
     * Called when the menu is closed. You never need to run this yourself
     * @param event The option choose event
     */
    public void run(EventType.MenuOptionChooseEvent event) {
        if (event.option == -1) {
            this.callback.accept(null);
        } else {
            int pageCount = (int) Math.ceil((double) options.size() / maxItemsPerPage);
            int itemsOnPage = pageCount == lastPage + 1 ? this.options.size() % maxItemsPerPage : maxItemsPerPage;
            if (itemsOnPage == 0) itemsOnPage = maxItemsPerPage;

            if (event.option >= itemsOnPage) {
                int newPage = event.option - itemsOnPage == 1 ? lastPage + 1 : lastPage - 1;
                newPage = Math.max(0, newPage);
                newPage = Math.min(pageCount - 1, newPage);
                open(event.player, newPage);
            } else {
                ArrayList<String> items = new ArrayList<>(this.options.values());
                String result = items.get(event.option + lastOffset);
                this.callback.accept(result);
            }
        }
    }
}
