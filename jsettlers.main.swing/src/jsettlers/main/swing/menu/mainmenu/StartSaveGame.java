package jsettlers.main.swing.menu.mainmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jsettlers.common.menu.IStartingGame;
import jsettlers.logic.map.save.loader.SavegameLoader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.swing.JSettlersFrame;
import jsettlers.main.swing.menu.openpanel.OpenPanel;

/**
 * @author codingberlin
 */
public class StartSaveGame implements ActionListener {

	private final JSettlersFrame settlersFrame;
	private OpenPanel relatedOpenPanel;

	public StartSaveGame(JSettlersFrame settlersFrame) {
		this.settlersFrame = settlersFrame;
	}

	public void setRelatedOpenPanel(OpenPanel relatedOpenPanel) {
		this.relatedOpenPanel = relatedOpenPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SavegameLoader savegameLoader = (SavegameLoader) relatedOpenPanel.getSelectedMap();
		if (savegameLoader != null) {
			// TODO: read playersettings out of savegame file
			long randomSeed = 4711L;
			byte playerId = 0;
			PlayerSetting[] playerSettings = PlayerSetting.createDefaultSettings(playerId, (byte) savegameLoader.getMaxPlayers());
			JSettlersGame game = new JSettlersGame(savegameLoader, randomSeed, playerId, playerSettings);
			IStartingGame startingGame = game.start();
			settlersFrame.showStartingGamePanel(startingGame);
		}
	}
}