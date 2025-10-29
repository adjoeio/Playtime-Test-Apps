export const humanizeMinutes = (minutes: number): string => {
    if (minutes > 60) {
        const hours = minutes / 60

        if (minutes > (60 * 24)) {
            const days = hours / 24
            const leftHours = hours % 24
            return `${days}d ${leftHours}h`
        }

        const leftMinutes = minutes % 60
        return `${hours}h ${leftMinutes}m`
    }

    return `${minutes}m 0s`
}
