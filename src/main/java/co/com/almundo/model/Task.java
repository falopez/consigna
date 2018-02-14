package co.com.almundo.model;

/**
 * Esta clase representa una llamada, a la 
 * cual le asignamos un id y una cola a la cual
 * fue asignada.
 */
public class Task {
	
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	private int id;

	
	public Task(){
		
	}
			
	/**
	 * Instantiates a new task.
	 *
	 * @param id the id
	 */
	public Task(int id) {
		super();
		this.id = id;
	}

	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}


	@Override
	public String toString() {
		return "Task [id=" + id  + "]";
	}
	
}
