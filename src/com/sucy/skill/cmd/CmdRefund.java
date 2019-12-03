/**
 * SkillAPI
 * com.sucy.skill.cmd.CmdRefund
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Steven Sucy, 2019 iomatix
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.data.Permissions;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Command for refunding invested skill points
 */
public class CmdRefund implements IFunction {
	private static final String CANNOT_USE = "cannot-use";
	private static final String NOT_PLAYER = "not-player";
	private static final String NO_CLASS = "no-class";
	private static final String REFUNDED_ALL = "refunded-all";
	private static final String REFUNDED_AP = "refunded-ap";
	private static final String REFUNDED_SP = "refunded-sp";
	private static final String NOT_PLAYER_OR_ARG = "not-player-or-argument";
	private static final String NO_PERMISSION = "no-permission";

	/**
	 * Runs the command
	 *
	 * @param cmd    command that was executed
	 * @param plugin plugin reference
	 * @param sender sender of the command
	 * @param args   argument list
	 */
	@Override
	public void execute(ConfigurableCommand cmd, Plugin plugin, CommandSender sender, String[] args) {

		// Players
		if (sender instanceof Player) {
			// Reset all skills or attr
			if (args.length >= 1) {
				if (args[0].toLowerCase().equals("attr") || args[0].toLowerCase().equals("points")) {
					PlayerData player = SkillAPI.getPlayerData((Player) sender);
					// Player must have a class
					if (!player.hasClass()) {
						cmd.sendMessage(sender, NO_CLASS, "&4You have not professed as any class yet.");
						return;
					}
					if (args[0].toLowerCase().equals("attr")) {
						player.refundAttributes();
						cmd.sendMessage(sender, REFUNDED_AP, "&2Your attribute points have been refunded.");
					} else if (args[0].toLowerCase().equals("points")) {
						player.refundSkills();
						cmd.sendMessage(sender, REFUNDED_SP, "&2Your skill points have been refunded.");
					}
				} else {
					if (!sender.hasPermission(Permissions.REFUND_OTHERS)) {
						cmd.sendMessage(sender, NO_PERMISSION, "&cYou are not allowed to run this command.");
						return;
					}
					OfflinePlayer target = VersionManager.getOfflinePlayer(args[0], false);
					if (target == null) {
						cmd.sendMessage(sender, NOT_PLAYER_OR_ARG,
								ChatColor.RED + "That is not a valid player name or argument.");
						return;
					}
					PlayerData player = SkillAPI.getPlayerData(target);
					// Player must have a class
					if (!player.hasClass()) {
						cmd.sendMessage(sender, NO_CLASS, "&4Target has not professed as any class yet.");
						return;
					}

					if (args.length >= 2) {
						if (args[1].toLowerCase().equals("attr")) {
							player.refundAttributes();
							if (target.isOnline()) {
								cmd.sendMessage(target.getPlayer(), REFUNDED_AP,
										"&2Your attribute points have been refunded.");
							}
						} else if (args[1].toLowerCase().equals("points")) {
							player.refundSkills();
							if (target.isOnline()) {
								cmd.sendMessage(target.getPlayer(), REFUNDED_SP,
										"&2Your skill points have been refunded.");
							}
						}
					} else {
						player.refundSkills();
						player.refundAttributes();
						if (target.isOnline()) {
							cmd.sendMessage(target.getPlayer(), REFUNDED_ALL,
									"&2Your skill points and attribute points have been refunded.");
						}
					}
				}
			} else {
				PlayerData player = SkillAPI.getPlayerData((Player) sender);
				player.refundSkills();
				player.refundAttributes();
				cmd.sendMessage(sender, REFUNDED_ALL, "&2Your skill points and attribute points have been refunded.");
			}

		}

		// Console
		else {
			if (args.length >= 1) {
				if (!sender.hasPermission(Permissions.REFUND_OTHERS)) {
					cmd.sendMessage(sender, NO_PERMISSION, "&cYou are not allowed to run this command.");
					return;
				}

				OfflinePlayer target = VersionManager.getOfflinePlayer(args[0], false);
				if (target == null) {
					cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "That is not a valid player name.");
					return;
				}
				PlayerData player = SkillAPI.getPlayerData(target);
				if (args.length >= 2) {
					if (args[1].toLowerCase().equals("attr")) {
						player.refundAttributes();
						if (target.isOnline()) {
							cmd.sendMessage(target.getPlayer(), REFUNDED_AP,
									"&2Your attribute points have been refunded.");
						}
					}else if (args[1].toLowerCase().equals("points")) {
						player.refundSkills();
						if (target.isOnline()) {
							cmd.sendMessage(target.getPlayer(), REFUNDED_SP, "&2Your skill points have been refunded.");
						}
					}
				} else {
					player.refundSkills();
					player.refundAttributes();
					if (target.isOnline()) {
						cmd.sendMessage(target.getPlayer(), REFUNDED_ALL,
								"&2Your skill points and attribute points have been refunded.");
					}
				}

			} else {
				cmd.sendMessage(sender, NOT_PLAYER, ChatColor.RED + "Specify a valid player name.");
			}
		}
	}
}
