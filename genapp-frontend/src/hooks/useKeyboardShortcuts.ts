/**
 * useKeyboardShortcuts Hook
 * Custom hook to handle keyboard shortcuts (e.g., Ctrl+K for search)
 */

import { useEffect, useCallback } from 'react';

export interface KeyboardShortcut {
  key: string;
  ctrlKey?: boolean;
  metaKey?: boolean;
  shiftKey?: boolean;
  callback: () => void;
}

/**
 * Custom hook to handle keyboard shortcuts
 * @param shortcuts - Array of keyboard shortcuts to handle
 */
export const useKeyboardShortcuts = (shortcuts: KeyboardShortcut[]) => {
  const handleKeyDown = useCallback(
    (event: KeyboardEvent) => {
      shortcuts.forEach((shortcut) => {
        const isKeyMatching = event.key.toLowerCase() === shortcut.key.toLowerCase();
        const isCtrlMatching = shortcut.ctrlKey ? event.ctrlKey || event.metaKey : true;
        const isShiftMatching = shortcut.shiftKey ? event.shiftKey : !event.shiftKey;

        if (isKeyMatching && isCtrlMatching && isShiftMatching) {
          event.preventDefault();
          shortcut.callback();
        }
      });
    },
    [shortcuts]
  );

  useEffect(() => {
    window.addEventListener('keydown', handleKeyDown);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [handleKeyDown]);
};

export default useKeyboardShortcuts;
