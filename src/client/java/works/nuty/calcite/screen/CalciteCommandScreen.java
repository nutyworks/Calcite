package works.nuty.calcite.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import works.nuty.calcite.widget.AutoActivateButtonWidget;
import works.nuty.calcite.widget.CalciteTextFieldWidget;
import works.nuty.calcite.widget.ModeButtonWidget;

import java.util.*;

@Environment(EnvType.CLIENT)
public class CalciteCommandScreen extends Screen {
    private final World world;
    private final BlockPos blockPos;
    private boolean isInitialized = false;
    private CommandListWidget commandListWidget;
    private ButtonWidget doneButton;
    private ButtonWidget cancelButton;
    @Nullable
    private Runnable commandSuggestorRenderer;

    public CalciteCommandScreen(CommandBlockBlockEntity initialBlockEntity) {
        super(NarratorManager.EMPTY);
        this.world = initialBlockEntity.getWorld();
        this.blockPos = initialBlockEntity.getPos();
    }

    public List<CommandBlockBlockEntity> getConnectedCommandBlocks() {
        if (this.client == null) return null;

        Set<BlockPos> visited = new HashSet<>();
        Set<Direction> directions = Set.of(Direction.UP, Direction.DOWN, Direction.EAST, Direction.SOUTH, Direction.NORTH, Direction.WEST);
        BlockPos current = null;
        BlockPos next = this.blockPos;
        int count = 1;

        while (count == 1) {
            current = next;
            count = 0;
            for (Direction d : directions) {
                BlockPos tempNext = current.offset(d);
                if (visited.contains(tempNext)) continue;

                BlockState blockState = this.world.getBlockState(tempNext);
                Block block = blockState.getBlock();
                boolean isCommandBlock = List.of(Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK).contains(block);
                if (!isCommandBlock) continue;

                Direction facing = blockState.get(CommandBlock.FACING);
                if (!current.equals(tempNext.offset(facing))) continue;

                visited.add(tempNext);

                next = tempNext;
                count++;
            }
        }

        List<CommandBlockBlockEntity> blockEntityHashMap = new ArrayList<>();
        BlockPos visit = current;
        visited.clear();

        while (true) {
            if (visited.contains(visit)) break;
            visited.add(visit);

            BlockState blockState = this.world.getBlockState(visit);
            Block block = blockState.getBlock();
            boolean isCommandBlock = List.of(Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK).contains(block);
            if (!isCommandBlock) break;

            blockEntityHashMap.add((CommandBlockBlockEntity) this.world.getBlockEntity(visit));

            Direction facing = blockState.get(CommandBlock.FACING);
            visit = visit.offset(facing);
        }

        return blockEntityHashMap;
    }

    @Nullable
    private AbstractCommandWidget getFocusedCommandWidget() {
        return this.commandListWidget.getFocused();
    }

    @Override
    protected void init() {
        this.commandListWidget = this.addDrawableChild(new CommandListWidget());

        if (!this.isInitialized) {
            var connectedCommandBlocks = this.getConnectedCommandBlocks();
            for (CommandBlockBlockEntity blockEntity : connectedCommandBlocks) {
                this.commandListWidget.addCommandBlock(blockEntity);
            }
            this.commandListWidget.addBlank();

            this.commandListWidget.setFocused(this.commandListWidget.positionedWidgets.get(this.blockPos));
            this.commandListWidget.scrollToFocused();

            for (CommandBlockBlockEntity blockEntity : connectedCommandBlocks) {
                assert this.client != null;
                assert this.client.getNetworkHandler() != null;
                this.client.getNetworkHandler().getDataQueryHandler().queryBlockNbt(blockEntity.getPos(), nbtCompound -> {
                    blockEntity.readNbt(nbtCompound);
                    this.commandListWidget.positionedWidgets.get(blockEntity.getPos()).updateCommandBlock();
                });
            }
        }

        this.doneButton = ButtonWidget.builder(ScreenTexts.DONE, button -> this.commitAndClose())
            .dimensions(this.width - 10 - 200 - 5, this.height - 25, 100, 20)
            .build();
        this.cancelButton = ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close())
            .dimensions(this.width - 10 - 100, this.height - 25, 100, 20)
            .build();
        this.isInitialized = true;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        CommandListWidget clw = this.commandListWidget;
        this.init(client, width, height);
        this.commandListWidget.apply(clw);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.commandListWidget.children().stream().noneMatch(AbstractCommandWidget::isModified);
    }

    private void commitAndClose() {
        for (CommandWidget widget : this.commandListWidget.positionedWidgets.values()) {
            widget.syncSettingsToServer();
        }
        assert this.client != null;
        this.client.setScreen(null);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.getFocusedCommandWidget() != null && this.getFocusedCommandWidget().charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.getFocusedCommandWidget() != null && this.getFocusedCommandWidget().keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            AbstractCommandWidget focused = this.commandListWidget.getFocused();
            if (focused != null) {
                this.commandListWidget.setFocused(null);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.getFocusedCommandWidget() != null && this.getFocusedCommandWidget().mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.doneButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.cancelButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        for (AbstractCommandWidget widget : this.commandListWidget.children()) {
            if (widget instanceof CommandWidget cw) {
                if (cw.modeButton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
                if (cw.autoActivateButton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        if (this.commandListWidget.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.commandListWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (commandSuggestorRenderer != null) {
            commandSuggestorRenderer.run();
            commandSuggestorRenderer = null;
        }

        doneButton.render(context, mouseX, mouseY, delta);
        cancelButton.render(context, mouseX, mouseY, delta);
    }

    @Environment(EnvType.CLIENT)
    public abstract static class AbstractCommandWidget extends ElementListWidget.Entry<AbstractCommandWidget> {
        abstract public boolean isModified();
    }

    @Environment(EnvType.CLIENT)
    public static class BlankCommandWidget extends AbstractCommandWidget {
        public BlankCommandWidget() {
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of();
        }

        @Override
        public List<? extends Element> children() {
            return List.of();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

        }

        public boolean isModified() {
            return false;
        }
    }

    @Environment(EnvType.CLIENT)
    public class CommandListWidget extends ElementListWidget<AbstractCommandWidget> {
        private List<CommandWidget> indexedWidgets;
        private Map<BlockPos, CommandWidget> positionedWidgets;

        public CommandListWidget() {
            super(CalciteCommandScreen.this.client, CalciteCommandScreen.this.width, CalciteCommandScreen.this.height, 0, 24);
            this.headerHeight = 5;
            this.indexedWidgets = new ArrayList<>();
            this.positionedWidgets = new HashMap<>();
            this.setRenderBackground(false);
        }

        protected void apply(CommandListWidget clw) {
            this.indexedWidgets = clw.indexedWidgets;
            this.positionedWidgets = clw.positionedWidgets;
            for (var child : clw.children()) {
                this.addEntry(child);
                if (child instanceof CommandWidget cw) {
                    cw.commandEdit.setWidth(CalciteCommandScreen.this.width - 70);
                }
            }
        }

        @Override
        protected int getScrollbarPositionX() {
            return this.width - 6;
        }

        @Override
        public int getRowWidth() {
            return this.width - 20;
        }

        public void addCommandBlock(CommandBlockBlockEntity blockEntity) {
            CommandWidget widget = new CommandWidget(blockEntity);
            this.indexedWidgets.add(widget);
            this.positionedWidgets.put(blockEntity.getPos(), widget);
            this.addEntry(widget);
        }

        public void addBlank() {
            this.addEntry(new BlankCommandWidget());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            AbstractCommandWidget widget = this.getFocused();
            if (widget instanceof CommandWidget cw) {
                if (cw.commandSuggestor.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return super.charTyped(chr, modifiers);
        }

        @Override
        public void setFocused(@Nullable Element focused) {
            super.setFocused(focused);

            if (focused instanceof CommandWidget commandWidget) {
                commandWidget.focusOn(commandWidget.commandEdit);
            }
        }

        public void scrollToFocused() {
            int index = indexedWidgets.indexOf(this.getFocused());
            int itemsOnScreen = this.height / this.itemHeight;
            this.setScrollAmount((index - itemsOnScreen / 2.0) * this.itemHeight);
        }
    }

    @Environment(EnvType.CLIENT)
    public class CommandWidget extends AbstractCommandWidget {
        protected final CalciteInputSuggestor commandSuggestor;
        protected final List<ClickableWidget> children;
        private final CommandBlockBlockEntity blockEntity;
        private final CalciteTextFieldWidget commandEdit;
        private final ModeButtonWidget modeButton;
        private final AutoActivateButtonWidget autoActivateButton;
        protected boolean loaded;
        protected boolean modified;
        private CommandBlockBlockEntity.Type mode;
        private boolean conditional;
        private boolean autoActivate;

        public CommandWidget(CommandBlockBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
            this.loaded = false;
            this.modified = false;

            this.commandEdit = new CalciteTextFieldWidget(CalciteCommandScreen.this.textRenderer, CalciteCommandScreen.this.width - 70, 20, Text.of(""));
            this.commandEdit.setMaxLength(32500);
            this.commandEdit.setChangedListener(this::onCommandChanged);

            this.commandSuggestor = new CalciteInputSuggestor(CalciteCommandScreen.this.client, CalciteCommandScreen.this, this.commandEdit, CalciteCommandScreen.this.textRenderer, true, true, 0, 7, Integer.MIN_VALUE);
            this.commandSuggestor.setWindowActive(true);
            this.commandSuggestor.refresh();

            this.commandEdit.setSuggestion(null);

            this.children = List.of(this.commandEdit);

            this.mode = CommandBlockBlockEntity.Type.AUTO;
            this.conditional = false;
            this.autoActivate = false;

            this.modeButton = new ModeButtonWidget(CalciteCommandScreen.this.textRenderer, 0, 0, 16, 16, new ModeButtonWidget.Callback() {

                @Override
                public void onModeChange(CommandBlockBlockEntity.Type mode) {
                    CommandWidget.this.mode = mode;
                    CommandWidget.this.modified = true;
                }

                @Override
                public void onConditionalChange(boolean conditional) {
                    CommandWidget.this.conditional = conditional;
                    CommandWidget.this.modified = true;
                }
            });
            this.autoActivateButton = new AutoActivateButtonWidget(CalciteCommandScreen.this.textRenderer, 0, 0, 16, 16, value -> {
                this.autoActivate = value;
                this.modified = true;
            });
        }

        public boolean isModified() {
            return loaded && (modified || !this.commandEdit.getText().equals(this.blockEntity.getCommandExecutor().getCommand()));
        }

        private void onCommandChanged(String text) {
            this.commandSuggestor.refresh();
        }

        public void updateCommandBlock() {
            CommandBlockExecutor commandBlockExecutor = this.blockEntity.getCommandExecutor();
            this.commandEdit.setText(commandBlockExecutor.getCommand());
            if (!this.isFocused()) {
                this.commandEdit.setCursorToStart(false);
                this.commandEdit.setSuggestion(null);
            }
//            boolean bl = commandBlockExecutor.isTrackingOutput();
            this.mode = this.blockEntity.getCommandBlockType();
            this.conditional = this.blockEntity.isConditionalCommandBlock();
            this.autoActivate = this.blockEntity.isAuto();
//            this.toggleTrackingOutputButton.setValue(bl);
            this.modeButton.mode = this.mode;
            this.modeButton.conditional = this.conditional;
            this.modeButton.active = true;

            this.autoActivateButton.value = this.autoActivate;
            this.autoActivateButton.active = true;
//            this.setPreviousOutputText(bl);
//            this.setButtonsActive(true);
            this.loaded = true;
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return super.charTyped(chr, modifiers);
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if (!focused) {
                this.commandEdit.setSuggestion(null);
                this.commandEdit.setFocused(false);
            } else {
                this.commandEdit.setFocused(true);
                this.commandEdit.setEditable(true);
                this.commandEdit.active = true;
                this.commandSuggestor.refresh();
            }
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return this.children;
        }

        @Override
        public List<? extends Element> children() {
            return this.children;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (this.commandSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            if (this.commandSuggestor.mouseScrolled(verticalAmount)) {
                return true;
            }
            return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.commandEdit.isFocused() && this.commandSuggestor.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.commandEdit.setX(50);
            this.commandEdit.setY(y);
            this.commandEdit.render(context, mouseX, mouseY, tickDelta);
            if (this.isModified()) {
                context.drawBorder(
                        this.commandEdit.getX(),
                        this.commandEdit.getY(),
                        this.commandEdit.getWidth(),
                        this.commandEdit.getHeight(),
                        this.commandEdit.isFocused() ? 0xFFFFFF00 : 0xFFA8A800
                );
            }

            if (this.isFocused()) {
                CalciteCommandScreen.this.commandSuggestorRenderer = () -> {
                    context.getMatrices().push();
                    context.getMatrices().translate(0, 0, 1);
                    CalciteInputSuggestor.SuggestionWindow window = this.commandSuggestor.window;
                    if (window != null) {
                        window.area.setY(window.calculateY(y));
                    }
                    this.commandSuggestor.render(context, mouseX, mouseY, y);
                    context.getMatrices().pop();
                };
            }

            if (this.loaded) {
                this.modeButton.setX(10);
                this.modeButton.setY(y + 2);
                this.modeButton.render(context, mouseX, mouseY, tickDelta);

                this.autoActivateButton.setX(30);
                this.autoActivateButton.setY(y + 2);
                this.autoActivateButton.render(context, mouseX, mouseY, tickDelta);
            }
        }

        protected void syncSettingsToServer() {
            if (!this.isModified()) return;

            assert CalciteCommandScreen.this.client != null;
            assert CalciteCommandScreen.this.client.getNetworkHandler() != null;
            CommandBlockExecutor commandExecutor = blockEntity.getCommandExecutor();
            CalciteCommandScreen.this.client.getNetworkHandler().sendPacket(new UpdateCommandBlockC2SPacket(BlockPos.ofFloored(commandExecutor.getPos()), this.commandEdit.getText(), this.mode, commandExecutor.isTrackingOutput(), this.conditional, this.autoActivate));
        }
    }
}