/**
 * Time-of-day greeting utility
 * Generates appropriate greeting based on current hour
 */

/**
 * Get greeting message based on hour of day
 * @param hour - Hour of day (0-23)
 * @returns Greeting message
 */
export const getTimeOfDayGreeting = (hour: number): string => {
  if (hour >= 6 && hour < 12) {
    return 'Good Morning';
  } else if (hour >= 12 && hour < 18) {
    return 'Good Afternoon';
  } else {
    return 'Good Evening';
  }
};

export default getTimeOfDayGreeting;
