package me.lpk.gui.tabs.internal;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import me.lpk.gui.controls.VerticalBar;
import me.lpk.gui.tabs.internal.handle.FieldHandler;
import me.lpk.gui.tabs.internal.handle.FrameHandler;
import me.lpk.gui.tabs.internal.handle.IincHandler;
import me.lpk.gui.tabs.internal.handle.InsnHandler;
import me.lpk.gui.tabs.internal.handle.IntHandler;
import me.lpk.gui.tabs.internal.handle.InvokeDynHandler;
import me.lpk.gui.tabs.internal.handle.JumpHandler;
import me.lpk.gui.tabs.internal.handle.LabelHandler;
import me.lpk.gui.tabs.internal.handle.LdcHandler;
import me.lpk.gui.tabs.internal.handle.LineHandler;
import me.lpk.gui.tabs.internal.handle.LookupHandler;
import me.lpk.gui.tabs.internal.handle.MethodHandler;
import me.lpk.gui.tabs.internal.handle.MultiArrayHandler;
import me.lpk.gui.tabs.internal.handle.NodeHandler;
import me.lpk.gui.tabs.internal.handle.SwitchHandler;
import me.lpk.gui.tabs.internal.handle.TypeHandler;
import me.lpk.gui.tabs.internal.handle.VarHandler;
import me.lpk.gui.tabs.treeview.TreeViewTab;

public class InternalBytecode extends InternalTab {
	private final StageSelection selection = new StageSelection();
	private final TextArea area = new TextArea();
	private Button btnChoose, btnSave;
	private ClassNode current;

	@Override
	protected VerticalBar<Button> createButtonList() {
		btnChoose = new Button("Choose Class");
		btnSave = new Button("Save");
		btnSave.setDisable(true);
		btnChoose.setOnAction(new ChooseClass());
		btnSave.setOnAction(new Save());
		//
		return new VerticalBar<Button>(1, btnChoose, btnSave);
	}

	@Override
	protected BorderPane createOtherStuff() {
		BorderPane bp = new BorderPane();
		return bp;
	}

	@Override
	public void targetLoaded() {

	}

	public void setNode(ClassNode newNode) {
		current = newNode;
		btnSave.setDisable(false);
		BorderPane bp = new BorderPane();
		VBox vb = new VBox(current.methods.size());
		for (MethodNode mn : current.methods) {
			Button button = new Button(mn.name);
			button.setOnAction(new ChooseMethod(mn));
			vb.getChildren().add(button);
		}
		bp.setCenter(vb);
		otherControls = bp;
		update();
	}

	/**
	 * Create a node window in the editor if the node is selected is valid.
	 */
	private void trySetNode() {
		TreeItem<String> item = selection.getSelected();
		if (item == null || item.getChildren().size() > 0) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		while (item.getParent() != null) {
			sb.insert(0, "/" + item.getValue());
			item = item.getParent();
		}
		String selected = sb.substring(1);
		if (selection.hasNode(selected)) {
			setNode(selection.getNodes().get(selected));
		}
	}

	private BorderPane genMethodText(MethodNode mn) {
		BorderPane bp = new BorderPane();
		area.clear();
		for (AbstractInsnNode ain : mn.instructions.toArray()) {
			area.appendText(handlers.get(ain.getType()).asText(ain));
		}
		bp.setCenter(area);
		return bp;
	}

	private final Map<Integer, NodeHandler> handlers = new HashMap<Integer, NodeHandler>(getHandlers());

	private Map<? extends Integer, ? extends NodeHandler> getHandlers() {
		HashMap<Integer, NodeHandler> handlers = new HashMap<Integer, NodeHandler>();
		handlers.put(AbstractInsnNode.FIELD_INSN, new FieldHandler());
		handlers.put(AbstractInsnNode.FRAME, new FrameHandler());
		handlers.put(AbstractInsnNode.IINC_INSN, new IincHandler());
		handlers.put(AbstractInsnNode.INT_INSN, new IntHandler());
		handlers.put(AbstractInsnNode.INVOKE_DYNAMIC_INSN, new InvokeDynHandler());
		handlers.put(AbstractInsnNode.JUMP_INSN, new JumpHandler());
		handlers.put(AbstractInsnNode.LABEL, new LabelHandler());
		handlers.put(AbstractInsnNode.LDC_INSN, new LdcHandler());
		handlers.put(AbstractInsnNode.LINE, new LineHandler());
		handlers.put(AbstractInsnNode.LOOKUPSWITCH_INSN, new LookupHandler());
		handlers.put(AbstractInsnNode.METHOD_INSN, new MethodHandler());
		handlers.put(AbstractInsnNode.MULTIANEWARRAY_INSN, new MultiArrayHandler());
		handlers.put(AbstractInsnNode.TABLESWITCH_INSN, new SwitchHandler());
		handlers.put(AbstractInsnNode.TYPE_INSN, new TypeHandler());
		handlers.put(AbstractInsnNode.VAR_INSN, new VarHandler());
		handlers.put(AbstractInsnNode.INSN, new InsnHandler());
		return handlers;
	}

	class ChooseClass implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (!selection.setup) {
				selection.setup();
				selection.targetLoaded();
			}
			otherControls = selection;
			update();
		}
	}

	class ChooseMethod implements EventHandler<ActionEvent> {
		private final MethodNode mn;

		public ChooseMethod(MethodNode mn) {
			this.mn = mn;
		}

		@Override
		public void handle(ActionEvent event) {
			otherControls = genMethodText(mn);
			update();
		}
	}

	class Save implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			// InsnList list = new InsnList();
			
			// TODO: Trash everything and cry.
			// Repeat the cycle until a decent GUI has been made. That or just gud the feature and shill qMatt's JBytedit (Yes, it's with one e ._.).
		}
	}

	class StageSelection extends TreeViewTab {
		boolean setup;

		@Override
		public EventHandler<MouseEvent> getClickEvent() {
			return new InternalChooseTree();
		}

		@Override
		protected VerticalBar<Button> createButtonList() {
			Button btn = new Button("Select");
			btn.setOnAction(new InternalChooseButton());
			setup = true;
			return new VerticalBar<Button>(1, btn);
		}
	}

	/**
	 * Choose event handler for the Button.
	 */
	class InternalChooseButton implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			trySetNode();
		}
	}

	/**
	 * Choose event handler for the TreeView.
	 */
	class InternalChooseTree implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			if (event.getClickCount() == 2) {
				trySetNode();
			}
		}
	}

}
