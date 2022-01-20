package com.openrsc.android.render;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;

import orsc.Config;
import orsc.enumerations.MessageTab;
import orsc.graphics.two.Fonts;
import orsc.mudclient;
import orsc.osConfig;

public class InputImpl implements OnGestureListener, OnKeyListener, OnTouchListener {

    private final mudclient mudclient;
    private final GestureDetector gestureDetector;
    private final AudioManager audioManager;

    public InputImpl(mudclient mudclient, View view) {
        this.mudclient = mudclient;
        this.view = view;
        gestureDetector = new GestureDetector(view.getContext(), this);
        audioManager = (AudioManager) view.getContext().getSystemService(Context.AUDIO_SERVICE);

        view.setOnTouchListener(this);
        view.setOnKeyListener(this);
    }

    private boolean isLongPress = false;
    private final View view;
    private long lastScrollOrRotate;

    @Override
    public boolean onDown(MotionEvent e) {
        if (osConfig.C_HOLD_AND_CHOOSE)
            return false;

        mudclient.mouseX = (int) (e.getX() / (getWidth() / (float) mudclient.getGameWidth()));
        mudclient.mouseY = (int) (e.getY() / (getHeight() / (float) (mudclient.getGameHeight() + 12)));
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        if (osConfig.C_HOLD_AND_CHOOSE)
            return;
        mudclient.mouseX = (int) (e.getX() / (getWidth() / (float) mudclient.getGameWidth()));
        mudclient.mouseY = (int) (e.getY() / (getHeight() / (float) (mudclient.getGameHeight() + 12)));
        mudclient.currentMouseButtonDown = 2;
        mudclient.lastMouseButtonDown = mudclient.currentMouseButtonDown;
        mudclient.lastMouseAction = 0;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (osConfig.C_HOLD_AND_CHOOSE)
            return false;
        mudclient.mouseX = (int) (e.getX() / (getWidth() / (float) mudclient.getGameWidth()));
        mudclient.mouseY = (int) (e.getY() / (getHeight() / (float) (mudclient.getGameHeight() + 12)));
        mudclient.currentMouseButtonDown = 1;
        mudclient.lastMouseButtonDown = mudclient.currentMouseButtonDown;
        mudclient.lastMouseAction = 0;
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mudclient.topMouseMenuVisible) {
            return true;
        }

        if (distanceY > 1)
            distanceY = 1;
        if (distanceY < -1)
            distanceY = -1;

        lastScrollOrRotate = System.currentTimeMillis();

        // Disables zoom while visible
        if (Config.S_SPAWN_AUCTION_NPCS && mudclient.auctionHouse.isVisible() || mudclient.onlineList.isVisible() || Config.S_WANT_SKILL_MENUS && mudclient.skillGuideInterface.isVisible()
                || Config.S_WANT_QUEST_MENUS && mudclient.questGuideInterface.isVisible() || mudclient.clan.getClanInterface().isVisible() || mudclient.party.getPartyInterface().isVisible() || mudclient.experienceConfigInterface.isVisible()
                || mudclient.ironmanInterface.isVisible() || mudclient.achievementInterface.isVisible() || Config.S_WANT_SKILL_MENUS && mudclient.doSkillInterface.isVisible()
                || Config.S_ITEMS_ON_DEATH_MENU && mudclient.lostOnDeathInterface.isVisible() || mudclient.territorySignupInterface.isVisible() || mudclient.messageTabSelected != MessageTab.ALL)
            return false;

        if (mudclient.showUiTab == 0) {
            if (Config.S_ZOOM_VIEW_TOGGLE || mudclient.getLocalPlayer().isStaff()) {
                if (osConfig.C_SWIPE_TO_ZOOM) {
                    int zoomDistance = (int) (-distanceY * 5);
                    int newZoom = osConfig.C_LAST_ZOOM + zoomDistance;
                    // Keep C_LAST_ZOOM aka the zoom increments on the range of [0, 255]
                    if (newZoom >= 0 && newZoom <= 255) {
                        osConfig.C_LAST_ZOOM = newZoom;
                    }
                }
            } else if (mudclient.cameraAllowPitchModification) {
                mudclient.cameraPitch = (mudclient.cameraPitch + (int) (-distanceY * 10)) & 1023;
            }

            if (osConfig.C_SWIPE_TO_ROTATE) {
                float clientDist = distanceX / (getWidth() / (float) mudclient.getGameWidth())
                        * 0.5F;
                mudclient.cameraRotation = (255 & mudclient.cameraRotation + (int) (clientDist));
            }

            return true;
        } else if (osConfig.C_SWIPE_TO_SCROLL) {
            mudclient.runScroll((int) distanceY);
            return true;
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        return false;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (osConfig.C_VOLUME_FUNCTION == 0) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                mudclient.keyLeft = event.getAction() == KeyEvent.ACTION_DOWN;
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                mudclient.keyRight = event.getAction() == KeyEvent.ACTION_DOWN;
                return true;
            }
        }
        // If we are not volume to rotate, then we are volume to zoom...
        else if (osConfig.C_VOLUME_FUNCTION == 1) {
            if (Config.S_ZOOM_VIEW_TOGGLE) {
                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    mudclient.keyUp = event.getAction() == KeyEvent.ACTION_DOWN;
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    mudclient.keyDown = event.getAction() == KeyEvent.ACTION_DOWN;
                    return true;
                }
            }
        }
        //
		else if (osConfig.C_VOLUME_FUNCTION == 2) {
			if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
				return true;
			}
			if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
				audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
				return true;
			}
		}

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int key = event.getUnicodeChar();
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                key = 8;
            }

            boolean hitInputFilter = false;

            for (int var5 = 0; var5 < Fonts.inputFilterChars.length(); ++var5) {
                if (Fonts.inputFilterChars.charAt(var5) == key) {
                    hitInputFilter = true;

                    break;
                }
            }

            mudclient.handleKeyPress((byte) 126, key);
            if (hitInputFilter && mudclient.inputTextCurrent.length() < 20) {
                mudclient.inputTextCurrent = mudclient.inputTextCurrent + (char) key;
            }

            if (hitInputFilter && mudclient.chatMessageInput.length() < 80) {
                mudclient.chatMessageInput = mudclient.chatMessageInput + (char) key;
            }

            // Backspace
            if (key == '\b' && mudclient.inputTextCurrent.length() > 0) {
                mudclient.inputTextCurrent = mudclient.inputTextCurrent.substring(0,
                        mudclient.inputTextCurrent.length() - 1);
            }

            // Backspace
            if (key == '\b' && mudclient.chatMessageInput.length() > 0) {
                mudclient.chatMessageInput = mudclient.chatMessageInput.substring(0,
                        mudclient.chatMessageInput.length() - 1);
            }

            if (key == 10 || key == 13) {
                mudclient.inputTextFinal = mudclient.inputTextCurrent;
                mudclient.chatMessageInputCommit = mudclient.chatMessageInput;
            }
            return true;
        }
        return false;
    }

    public boolean onTouch(View v, MotionEvent e) {
        mudclient.mouseX = (int) (e.getX() / (getWidth() / (float) mudclient.getGameWidth()));
        mudclient.mouseY = (int) (e.getY() / (getHeight() / (float) (mudclient.getGameHeight() + 12)));
        mudclient.lastMouseAction = 0;

        if (!gestureDetector.onTouchEvent(e)) {
            if (osConfig.C_HOLD_AND_CHOOSE) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_UP:
                        isLongPress = false;
                        if (mudclient.topMouseMenuVisible) {
                            int width = mudclient.menuCommon.getWidth();
                            int height = mudclient.menuCommon.getHeight();
                            if (mudclient.menuX - 10 <= mudclient.mouseX && mudclient.menuY - 10 <= mudclient.mouseY
                                    && width + mudclient.menuX + 10 >= mudclient.mouseX
                                    && mudclient.mouseY <= 10 + mudclient.menuY + height) {
                                mudclient.lastMouseButtonDown = mudclient.currentMouseButtonDown = 1;
                                return true;
                            } else {
                                mudclient.topMouseMenuVisible = false;
                                return true;
                            }
                        }
                        if (System.currentTimeMillis() - lastScrollOrRotate < 100) {
                            return true;
                        }
                        mudclient.lastMouseButtonDown = mudclient.currentMouseButtonDown = 1;
                        break;
                    case MotionEvent.ACTION_DOWN:
                        mudclient.lastMouseButtonDown = mudclient.currentMouseButtonDown = 0;
                        if (!isLongPress) {
                            isLongPress = true;
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                if (System.currentTimeMillis() - lastScrollOrRotate < 100) {
                                    return;
                                }
                                if (isLongPress) {
                                    mudclient.lastMouseButtonDown = mudclient.currentMouseButtonDown = 2;
                                }
                            }, osConfig.C_LONG_PRESS_TIMER * 50L);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (e.getDownTime() > 0) {
                            mudclient.currentMouseButtonDown = 1;
                        }
                        break;
                }
            }
        }

        return false;
    }

    private float getHeight() {
        return view.getHeight();
    }

    private float getWidth() {
        return view.getWidth();
    }
}
