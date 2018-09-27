package dhz.rscarchiver;

import jagex.IO.Archive;
import jagex.IO.DataUtils;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author DeadHeadedZed
 * 
 * This is just basically a GUI frontend for messing with the bz2 compressed archives RSC uses
 * 
 * There is a command line model converter that does similar stuff here:
 * https://github.com/MarkGore/RSCModelsConvert
 *
 */
public class RscArchiver implements ActionListener, ListSelectionListener, WindowListener
{
	public static final String version = "1.0.1";
	
	final JFileChooser fileChooser;
	private JFrame frameMain;
	private JList listArchiveFiles;
	private JLabel archiveName;
	private JLabel archiveSelectedItems;
	private Archive archive;
	private boolean changes;
	private File saveLocation;
	
	public static void main(String[] args)
	{
		new RscArchiver();
	}
	
	public RscArchiver()
	{	
		changes = false;
		archive = null;
		saveLocation = null;
		
		fileChooser = new JFileChooser();
		
		frameMain = new JFrame(); //uses default borderlayout
		frameMain.setTitle("DHZ's RSC Archiver " + version);
		frameMain.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frameMain.addWindowListener(this);
		frameMain.setPreferredSize(new Dimension(600, 600));	
		
		JMenuBar menuBarMain = new JMenuBar();
		
		JMenu menuFile = new JMenu("File");
		
		JMenuItem menuItemFileNew = new JMenuItem("New");
		menuItemFileNew.setActionCommand("openNew");
		menuItemFileNew.addActionListener(this);
		
		JMenuItem menuItemFileOpen = new JMenuItem("Open");
		menuItemFileOpen.setActionCommand("openArchive");
		menuItemFileOpen.addActionListener(this);
		
		JMenuItem menuItemFileSave = new JMenuItem("Save");
		menuItemFileSave.setActionCommand("save");
		menuItemFileSave.addActionListener(this);
		
		JMenuItem menuItemFileSaveAs = new JMenuItem("Save As...");
		menuItemFileSaveAs.setActionCommand("saveAs");
		menuItemFileSaveAs.addActionListener(this);
		
		JMenuItem menuItemFileExit = new JMenuItem("Exit");
		menuItemFileExit.setActionCommand("exitProgram");
		menuItemFileExit.addActionListener(this);
		
		menuFile.add(menuItemFileNew);
		menuFile.add(menuItemFileOpen);
		menuFile.addSeparator();
		menuFile.add(menuItemFileSave);
		menuFile.add(menuItemFileSaveAs);
		menuFile.addSeparator();
		menuFile.add(menuItemFileExit);
		
		JMenu menuEdit = new JMenu("Edit");
		
		JMenuItem menuItemEditClear = new JMenuItem("Deselect All");
		menuItemEditClear.setActionCommand("clearSelection");
		menuItemEditClear.addActionListener(this);
		
		JMenuItem menuItemEditAll = new JMenuItem("Select All");
		menuItemEditAll.setActionCommand("selectAll");
		menuItemEditAll.addActionListener(this);
		
		JMenuItem menuItemEditDelete = new JMenuItem("Delete Selected");
		menuItemEditDelete.setActionCommand("delete");
		menuItemEditDelete.addActionListener(this);
		
		menuEdit.add(menuItemEditClear);
		menuEdit.add(menuItemEditAll);
		menuEdit.addSeparator();
		menuEdit.add(menuItemEditDelete);
		
		JMenu menuImport = new JMenu("Import");
		
		JMenuItem menuItemImport = new JMenuItem("Import...");
		menuItemImport.setActionCommand("import");
		menuItemImport.addActionListener(this);
		
		JMenuItem menuItemReplace = new JMenuItem("Replace Single...");
		menuItemReplace.setActionCommand("replace");
		menuItemReplace.addActionListener(this);
		
		menuImport.add(menuItemImport);
		menuImport.add(menuItemReplace);
		
		JMenu menuExport = new JMenu("Export");
		
		JMenuItem menuItemExport1 = new JMenuItem("Export Single As...");
		menuItemExport1.setActionCommand("export1");
		menuItemExport1.addActionListener(this);
		
		JMenuItem menuItemExportSelected = new JMenuItem("Export Selected...");
		menuItemExportSelected.setActionCommand("exportSelected");
		menuItemExportSelected.addActionListener(this);
		
		menuExport.add(menuItemExport1);
		menuExport.add(menuItemExportSelected);
		
		JMenu menuSearch = new JMenu("Search");
		
		JMenuItem menuItemFindName = new JMenuItem("Find By Filename");
		menuItemFindName.setActionCommand("findByName");
		menuItemFindName.addActionListener(this);
		
		JMenuItem menuItemFindHash = new JMenuItem("Find By Hash");
		menuItemFindHash.setActionCommand("findByHash");
		menuItemFindHash.addActionListener(this);
		
		menuSearch.add(menuItemFindName);
		menuSearch.add(menuItemFindHash);
		
		JMenu menuHelp = new JMenu("Help");
		
		JMenuItem menuItemHelpHelp = new JMenuItem("Help");
		menuItemHelpHelp.setActionCommand("showHelp");
		menuItemHelpHelp.addActionListener(this);
		
		menuHelp.add(menuItemHelpHelp);
		
		menuBarMain.add(menuFile);
		menuBarMain.add(menuEdit);
		menuBarMain.add(menuImport);
		menuBarMain.add(menuExport);
		menuBarMain.add(menuSearch);
		menuBarMain.add(menuHelp);
		
		listArchiveFiles = new JList();
		listArchiveFiles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		listArchiveFiles.setLayoutOrientation(JList.VERTICAL);
		listArchiveFiles.addListSelectionListener(this);
		listArchiveFiles.setVisibleRowCount(-1);
		
		JScrollPane listScroller = new JScrollPane(listArchiveFiles);
		
		JPanel panelStatus = new JPanel();
		panelStatus.setLayout(new BoxLayout(panelStatus, BoxLayout.X_AXIS));
		
		archiveName = new JLabel("Archive:");
		archiveSelectedItems = new JLabel("Selected: 0/0");
		
		panelStatus.add(archiveName);
		panelStatus.add(Box.createHorizontalGlue());
		panelStatus.add(archiveSelectedItems);
			
		frameMain.setJMenuBar(menuBarMain);
		frameMain.getContentPane().add(listScroller, BorderLayout.CENTER);
		frameMain.getContentPane().add(panelStatus, BorderLayout.PAGE_END);
		frameMain.pack();
		frameMain.setVisible(true);
	}
	
	public JFrame getFrame()
	{
		return frameMain;
	}
	
	private void exitProgram()
	{
		if(changes)
		{
			if(!show2OptionDialog("Exit without saving?", "Are you sure you want to quit?\nYou have unsaved changes.", "Cancel", "Exit"))
				System.exit(0);
		}
		else
			System.exit(0);
	}
	
	private void openNew()
	{
		if(archive == null)
		{
			archive = new Archive();
			saveLocation = null;
			archiveName.setText("Archive: Unsaved archive");
			changes = false;
			refreshSelectedCount();
			refreshList();	
		}
		else
		{
			if(changes)
			{
				if(!show2OptionDialog("Continue without saving?", "Are you sure you want to contine?\nYou have unsaved changes.", "Cancel", "New Archive"))
				{
					archive = new Archive();
					saveLocation = null;
					archiveName.setText("Archive: Unsaved archive");
					changes = false;
					refreshSelectedCount();
					refreshList();
				}
			}
			else
			{
				archive = new Archive();
				saveLocation = null;
				archiveName.setText("Archive: Unsaved archive");
				changes = false;
				refreshSelectedCount();
				refreshList();
			}
		}
	}
	
	private void openArchive()
	{
		//hacky attempt at clearing the fc
		File currentDirectory = fileChooser.getCurrentDirectory();
		fileChooser.setSelectedFile(new File(""));
		fileChooser.setCurrentDirectory(currentDirectory);
				
		int retVal = fileChooser.showOpenDialog(frameMain);
		
		if(retVal == JFileChooser.APPROVE_OPTION)
		{
			File selected = fileChooser.getSelectedFile();
			Archive newArchive = null;
			try
			{
				newArchive = new Archive(Util.getFileBytes(selected));
			}
			catch(IOException ioe)
			{
				JOptionPane.showMessageDialog(frameMain, "IO Error: Could not read the file");
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(frameMain, "Couldn't open file, is it a RSC archive?");
			}
			if(newArchive != null)
			{
				archive = newArchive;
				archiveName.setText("Archive: " + selected.getName());
				saveLocation = selected;
				refreshSelectedCount();
				refreshList();
			}
		}
	}
	
	private void saveArchive()
	{
		if(changes)
		{
			if(archive != null)
			{
				if(archive.getNumFiles() > 1)
				{
					if(saveLocation == null)
					{
						saveArchiveAs();
					}
					else
					{
						try
						{
							byte[] refreshData = archive.recompile();
							DataUtils.writeFile(saveLocation, refreshData);
							archive = new Archive(refreshData);
							changes = false;
						}
						catch(IOException ioe)
						{
							JOptionPane.showMessageDialog(frameMain, "IO Error: Could not save the file");
						}
					}
				}
				else
					JOptionPane.showMessageDialog(frameMain, "Archives must have at least 2 files");
			}
			else
				JOptionPane.showMessageDialog(frameMain, "No open archive to save");
		}
	}
	
	private void saveArchiveAs()
	{
		if(archive != null)
		{
			if(archive.getNumFiles() > 1)
			{
				//hacky attempt at clearing the fc
				File currentDirectory = fileChooser.getCurrentDirectory();
				fileChooser.setSelectedFile(new File(""));
				fileChooser.setCurrentDirectory(currentDirectory);
				
				int retVal = fileChooser.showSaveDialog(frameMain);
				
				if(retVal == fileChooser.APPROVE_OPTION)
				{
					File selected = fileChooser.getSelectedFile();
					if(selected.exists())
					{
						if(!show2OptionDialog("Overwrite?", "Do you want to overwrite this existing file?", "Cancel", "Overwrite"))
						{
							try
							{
								byte[] refreshData = archive.recompile();
								DataUtils.writeFile(saveLocation, refreshData);
								archive = new Archive(refreshData);
								saveLocation = selected;
								archiveName.setText("Archive: " + saveLocation.getName());
								changes = false;
							}
							catch(IOException ioe)
							{
								JOptionPane.showMessageDialog(frameMain, "IO Error: Could not save the file");
							}
						}
					}
					else
					{
						try
						{
							byte[] refreshData = archive.recompile();
							DataUtils.writeFile(saveLocation, refreshData);
							archive = new Archive(refreshData);
							saveLocation = selected;
							archiveName.setText("Archive: " + saveLocation.getName());
							changes = false;
						}
						catch(IOException ioe)
						{
							JOptionPane.showMessageDialog(frameMain, "IO Error: Could not save the file");
						}	
					}
				}
			}
			else
				JOptionPane.showMessageDialog(frameMain, "Archives must have at least 2 files");
		}
		else
			JOptionPane.showMessageDialog(frameMain, "No open archive to save");
	}
	
	private void delete()
	{
		if(archive != null)
		{
			int[] selected = listArchiveFiles.getSelectedIndices();
			if(selected.length > 0)
			{
				for(int i = selected.length - 1; i >= 0; i--)
				{
					archive.removeFile(selected[i]);
				}
				changes = true;
				refreshList();
				refreshSelectedCount();
			}
		}
		else
			JOptionPane.showMessageDialog(frameMain, "No open archive to edit");
	}
	
	private void clearSelection()
	{
		if(archive != null)
		{
			listArchiveFiles.clearSelection();
			refreshSelectedCount();
		}
	}
	
	private void selectAll()
	{
		if(archive != null)
		{
			listArchiveFiles.setSelectionInterval(0, archive.getNumFiles() - 1);
			refreshSelectedCount();
		}
	}
	
	private void refreshList()
	{
		listArchiveFiles.setListData(archive.getNames());
	}
	
	private void refreshSelectedCount()
	{
		int selected = listArchiveFiles.getSelectedIndices().length;
		int max = archive.getNumFiles();
		archiveSelectedItems.setText("Selected: " + selected + "/" + max);
	}
	
	private void findByName()
	{
		if(archive != null)
		{
			String term = JOptionPane.showInputDialog(frameMain, "Enter the filename (with extension) you want to find", "Search By Filename", JOptionPane.PLAIN_MESSAGE);
			int indx = archive.getIndexForName(term);
			if(indx == -1)
			{
				listArchiveFiles.clearSelection();
				JOptionPane.showMessageDialog(frameMain, "That hash could not be found");
			}
			else
			{
				listArchiveFiles.setSelectedIndex(indx);
				listArchiveFiles.ensureIndexIsVisible(indx);
			}
		}
		else
			JOptionPane.showMessageDialog(frameMain, "Open an archive first");
	}
	
	private void findByHash()
	{
		if(archive != null)
		{
			String term = JOptionPane.showInputDialog(frameMain, "Enter the hash (integer) you want to find", "Search By Hash", JOptionPane.PLAIN_MESSAGE);
			if(Util.isInt(term))
			{
				int termInt = Integer.parseInt(term);
				int indx = archive.getIndexForHash(termInt);
				if(indx == -1)
				{
					listArchiveFiles.clearSelection();
					JOptionPane.showMessageDialog(frameMain, "That hash could not be found");
				}
				else
				{
					listArchiveFiles.setSelectedIndex(indx);
					listArchiveFiles.ensureIndexIsVisible(indx);
				}
			}
		}
		else
			JOptionPane.showMessageDialog(frameMain, "Open an archive first");
	}
	
	private void importFiles()
	{
		if(archive != null)
		{
			//hacky attempt at clearing the fc
			File currentDirectory = fileChooser.getCurrentDirectory();
			fileChooser.setSelectedFile(new File(""));
			fileChooser.setCurrentDirectory(currentDirectory);
			
			fileChooser.setMultiSelectionEnabled(true);
			int retVal = fileChooser.showDialog(frameMain, "Import");
			
			if(retVal == fileChooser.APPROVE_OPTION)
			{
				boolean overwriteAll = false;
				File[] imports = fileChooser.getSelectedFiles();
				for(int i = 0; i < imports.length; i++)
				{
					String filename = imports[i].getName();
					int hash = archive.getHash(filename);
					int exists = archive.getIndexForHash(hash);
					if(exists == -1) //archive does not have a file with the same hash
					{
						byte[] data = null;
						try
						{
							data = Util.fileToBytes(imports[i]);
						}
						catch(IOException ioe)
						{
							if(i == imports.length - 1) //no more imports
							{
								JOptionPane.showMessageDialog(frameMain, "There was an error importing the file: " + filename + 
										"\nIt was skipped.", "Import Error", JOptionPane.WARNING_MESSAGE);
								break;
							}
							else //there are more files to import
							{
								if(show2OptionDialog("Import Error", "There was an error importing the file: " + filename +
										"\nDo you want to skip it or stop the rest of the import?", "Skip", "Cancel"))
									continue; //skip file
								break; //cancel import	
							}
						}
						if(data.length < 1)
						{
							if(i == imports.length - 1) //no more imports
							{
								JOptionPane.showMessageDialog(frameMain, "" + filename + " was empty." + 
										"\nIt was skipped.", "Import Error", JOptionPane.WARNING_MESSAGE);
								break;
							}
							else //there are more files to import
							{
								if(show2OptionDialog("Empty File", "" + filename + " was empty." +
										"\nDo you want to skip it or stop the rest of the import?", "Skip", "Cancel"))
									continue; //skip file
								break; //cancel import	
							}
						}
						archive.addFile(hash, data);
						changes = true;
					}
					else //archive already has a file with the same hash
					{						
						//Check for overwrite
						int ret = 1;
						if(!overwriteAll)
						{
							ret = show3OptionDialog("Overwrite?", "" + filename + " collides with existing hash: " + hash +
								"\nDo you want to overwrite it or skip it?", "Overwrite", "Overwrite All", "Skip");
						}
						if(ret == 0)//overwrite
						{
							byte[] data = null;
							try
							{
								data = Util.fileToBytes(imports[i]);
							}
							catch(IOException ioe)
							{
								if(i == imports.length - 1) //no more imports
								{
									JOptionPane.showMessageDialog(frameMain, "There was an error importing the file: " + filename + 
											"\nIt was skipped.", "Import Error", JOptionPane.WARNING_MESSAGE);
									break;
								}
								else //there are more files to import
								{
									if(show2OptionDialog("Import Error", "There was an error importing the file: " + filename +
											"\nDo you want to skip it or stop the rest of the import?", "Skip", "Cancel"))
										continue; //skip file
									break; //cancel import	
								}
							}
							if(data.length < 1)
							{
								if(i == imports.length - 1) //no more imports
								{
									JOptionPane.showMessageDialog(frameMain, "" + filename + " was empty." + 
											"\nIt was skipped.", "Import Error", JOptionPane.WARNING_MESSAGE);
									break;
								}
								else //there are more files to import
								{
									if(show2OptionDialog("Empty File", "" + filename + " was empty." +
											"\nDo you want to skip it or stop the rest of the import?", "Skip", "Cancel"))
										continue; //skip file
									break; //cancel import	
								}
							}
							archive.updateFile(exists, data);
							changes = true;
						}
						else if(ret == 1)//overwrite all
						{
							overwriteAll = true;
							byte[] data = null;
							try
							{
								data = Util.fileToBytes(imports[i]);
							}
							catch(IOException ioe)
							{
								if(i == imports.length - 1) //no more imports
								{
									JOptionPane.showMessageDialog(frameMain, "There was an error importing the file: " + filename + 
											"\nIt was skipped.", "Import Error", JOptionPane.WARNING_MESSAGE);
									break;
								}
								else //there are more files to import
								{
									if(show2OptionDialog("Import Error", "There was an error importing the file: " + filename +
											"\nDo you want to skip it or stop the rest of the import?", "Skip", "Cancel"))
										continue; //skip file
									break; //cancel import	
								}
							}
							if(data.length < 1)
							{
								if(i == imports.length - 1) //no more imports
								{
									JOptionPane.showMessageDialog(frameMain, "" + filename + " was empty." + 
											"\nIt was skipped.", "Import Error", JOptionPane.WARNING_MESSAGE);
									break;
								}
								else //there are more files to import
								{
									if(show2OptionDialog("Empty File", "" + filename + " was empty." +
											"\nDo you want to skip it or stop the rest of the import?", "Skip", "Cancel"))
										continue; //skip file
									break; //cancel import	
								}
							}
							archive.updateFile(exists, data);
							changes = true;
						}
						else if(ret == 2)//skip
							continue;
					}
					refreshList();
					refreshSelectedCount();
				}
			}
			fileChooser.setMultiSelectionEnabled(false);
		}
		else
			JOptionPane.showMessageDialog(frameMain, "Open an archive first");
	}
	
	private void replace()
	{
		if(archive != null)
		{
			int[] selected = listArchiveFiles.getSelectedIndices();
			if(selected.length == 1)
			{
				//hacky attempt at clearing the fc
				File currentDirectory = fileChooser.getCurrentDirectory();
				fileChooser.setSelectedFile(new File(""));
				fileChooser.setCurrentDirectory(currentDirectory);
				
				int retVal = fileChooser.showDialog(frameMain, "Replace");
				
				if(retVal == fileChooser.APPROVE_OPTION)
				{
					File selectedFile = fileChooser.getSelectedFile();
					byte[] data = null;
					try
					{
						data = Util.fileToBytes(selectedFile);
					}
					catch(IOException ioe)
					{
						JOptionPane.showMessageDialog(frameMain, "There was an error importing the file.", "Import Error", JOptionPane.WARNING_MESSAGE);
					}
					if(data != null)
					{
						archive.updateFile(selected[0], data);
						changes = true;
					}
				}
			}
			else
				JOptionPane.showMessageDialog(frameMain, "Select only 1 entry");
		}
		else
			JOptionPane.showMessageDialog(frameMain, "Open an archive first");
	}
	
	private void exportSingle()
	{
		if(archive != null)
		{
			int[] selected = listArchiveFiles.getSelectedIndices();
			if(selected.length == 1)
			{
				//hacky attempt at clearing the fc
				File currentDirectory = fileChooser.getCurrentDirectory();
				fileChooser.setSelectedFile(new File(""));
				fileChooser.setCurrentDirectory(currentDirectory);
				
				int retVal = fileChooser.showSaveDialog(frameMain);
				
				if(retVal == fileChooser.APPROVE_OPTION)
				{
					File selectedFile = fileChooser.getSelectedFile();
					if(selectedFile.exists())
					{
						if(!show2OptionDialog("Overwrite?", "Do you want to overwrite this existing file?", "Cancel", "Overwrite"))
						{
							try
							{
								DataUtils.writeFile(selectedFile, archive.getFileAt(selected[0]));
							}
							catch(IOException ioe)
							{
								JOptionPane.showMessageDialog(frameMain, "IO Error: Could not export the file");
							}
						}
					}
					else
					{
						try
						{
							DataUtils.writeFile(selectedFile, archive.getFileAt(selected[0]));
						}
						catch(IOException ioe)
						{
							JOptionPane.showMessageDialog(frameMain, "IO Error: Could not export the file: ");
						}
					}
				}
			}
			else
				JOptionPane.showMessageDialog(frameMain, "Select only 1 entry");
		}
		else
			JOptionPane.showMessageDialog(frameMain, "Open an archive first");
	}
	
	private void exportSelected()
	{
		if(archive != null)
		{
			int[] selected = listArchiveFiles.getSelectedIndices();
			if(selected.length > 0)
			{
				//hacky attempt at clearing the fc
				File currentDirectory = fileChooser.getCurrentDirectory();
				fileChooser.setSelectedFile(new File(""));
				fileChooser.setCurrentDirectory(currentDirectory);
				
				fileChooser.setFileSelectionMode(fileChooser.DIRECTORIES_ONLY);
				int retVal = fileChooser.showDialog(frameMain, "Export");
				
				File dir = fileChooser.getSelectedFile();
				
				if(retVal == fileChooser.APPROVE_OPTION && dir.exists())
				{
					boolean overwriteAll = false;
					for(int i = 0; i < selected.length; i++)
					{
						String identifier = archive.getName(selected[i]);
						File newFile = new File(dir.getAbsolutePath() + File.separator + identifier);
						
						if(newFile.exists())
						{
							int ret = 1;
							if(!overwriteAll)
							{
								ret = show3OptionDialog("Overwrite?", "" + identifier + " collides with existing file." +
										"\nDo you want to overwrite it or skip it?", "Overwrite", "Overwrite All", "Skip");
							}
							if(ret == 0) //overwrite
							{
								try
								{
									DataUtils.writeFile(newFile, archive.getFileAt(selected[i]));
								}
								catch(IOException ioe)
								{
									if(i == selected.length - 1)
									{
										JOptionPane.showMessageDialog(frameMain, "Could not export: " + identifier + 
												"\nIt was skipped.", "Export Error", JOptionPane.WARNING_MESSAGE);
										break;
									}
									else
									{
										if(show2OptionDialog("Continue?", "There was an error exporting file: " + identifier +
												"/nDo you want to skip it or cancel the export?", "Skip", "Cancel"))
											continue;
										break;	
									}
								}
							}
							else if(ret == 1) //overwrite all
							{
								overwriteAll = true;
								try
								{
									DataUtils.writeFile(newFile, archive.getFileAt(selected[i]));
								}
								catch(IOException ioe)
								{
									if(i == selected.length - 1)
									{
										JOptionPane.showMessageDialog(frameMain, "Could not export: " + identifier + 
												"\nIt was skipped.", "Export Error", JOptionPane.WARNING_MESSAGE);
										break;
									}
									else
									{
										if(show2OptionDialog("Continue?", "There was an error exporting file: " + identifier +
												"/nDo you want to skip it or cancel the export?", "Skip", "Cancel"))
											continue;
										break;	
									}
								}
							}
							else // skip
								continue;
						}
						else //file doesn't exist
						{
							try
							{
								DataUtils.writeFile(newFile, archive.getFileAt(selected[i]));
							}
							catch(IOException ioe)
							{
								if(i == selected.length - 1)
								{
									JOptionPane.showMessageDialog(frameMain, "Could not export: " + identifier + 
											"\nIt was skipped.", "Export Error", JOptionPane.WARNING_MESSAGE);
								}
								else
								{
									if(show2OptionDialog("Continue?", "There was an error exporting file: " + identifier +
											"/nDo you want to skip it or cancel the export?", "Skip", "Cancel"))
										continue;
									break;	
								}
							}
						}
					}
				}
				else if(!dir.exists())
				{
					JOptionPane.showMessageDialog(frameMain, "That directory doesn't exist.");
				}
				fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY);
			}
		}
		else
			JOptionPane.showMessageDialog(frameMain, "Open an archive first");
	}
	
	private void showHelp()
	{
		if(Desktop.isDesktopSupported())
		{
			try
			{
				Desktop.getDesktop().browse(new URI("https://github.com/DeadHeadedZed/DHZ-RSC-Archiver"));
			}
			catch(Exception e)
			{
			}
		}
	}


	private boolean show2OptionDialog(String title, String msg, String opt0, String opt1)
	{
		Object[] options = {opt0, opt1};
		int ret = JOptionPane.showOptionDialog(frameMain, msg,
					title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if(ret == 0)
			return true;
		return false;
	}
	
	private int show3OptionDialog(String title, String msg, String opt0, String opt1, String opt2)
	{
		Object[] options = {opt0, opt1, opt2};
		return JOptionPane.showOptionDialog(frameMain, msg, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae)
	{
		switch (ae.getActionCommand())
		{
			case "exitProgram":
				exitProgram();
				break;
				
			case "openNew":
				openNew();
				break;
				
			case "openArchive":
				openArchive();
				break;
				
			case "save":
				saveArchive();
				break;
				
			case "saveAs":
				saveArchiveAs();
				break;
				
			case "clearSelection":
				clearSelection();
				break;
				
			case "selectAll":
				selectAll();
				break;
				
			case "delete":
				delete();
				break;
				
			case "export1":
				exportSingle();
				break;
				
			case "exportSelected":
				exportSelected();
				break;
				
			case "findByName":
				findByName();
				break;
				
			case "findByHash":
				findByHash();
				break;
				
			case "import":
				importFiles();
				break;
				
			case "replace":
				replace();
				break;
				
			case "showHelp":
				showHelp();
				break;
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		refreshSelectedCount();
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		exitProgram();
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		exitProgram();
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
	}

	@Override
	public void windowIconified(WindowEvent e)
	{
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
	}
}
