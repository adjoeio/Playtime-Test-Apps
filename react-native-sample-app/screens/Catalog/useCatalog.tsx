import { useCallback, useEffect, useMemo, useState } from 'react';
import Playtime, { PlaytimeStudio } from 'react-native-adjoe-sdk';
import { PlaytimeCampaign } from 'react-native-adjoe-sdk/src-ts/common/PlaytimeStudio';
import { SDK_HASH, USER_ID } from '../../util/constants';
import { Platform } from 'react-native';
import { requestTrackingPermission } from 'react-native-tracking-transparency';

export const useCatalog = () => {
    const [tosChecked, setTosChecked] = useState<boolean>();
    const [permissionsChecked, setPermissionsChecked] = useState<boolean>();
    const [activeTab, setActiveTab] = useState<'catalog' | 'installed'>('catalog');
    const [catalogCampaigns, setCatalogCampaigns] = useState<PlaytimeCampaign[]>([]);
    const [installedCampaigns, setInstalledCampaigns] = useState<PlaytimeCampaign[]>([]);
    const [clickedCampaign, setClickedCampaign] = useState<PlaytimeCampaign | null>(null);
    const [optionsAreSet, setOptionsAreSet] = useState(false);

    const loading = useMemo(() => {
        return !(typeof tosChecked === 'boolean' && typeof permissionsChecked === 'boolean') && !optionsAreSet;
    }, [tosChecked, permissionsChecked, optionsAreSet]);

    const checkUsagePermission = useCallback(async () => {
        if (Platform.OS === 'ios') {
            const trackingStatus = await requestTrackingPermission();
            if (trackingStatus === 'authorized' || trackingStatus === 'unavailable') {
                setTosChecked(true); // Auto TOS
                setPermissionsChecked(true);
            } else {
                setTosChecked(true); // Auto TOS
                setPermissionsChecked(false);
            }
            return null;
        }

        try {
            const val = await PlaytimeStudio.getPermissions();
            setTosChecked(val.permissions.isTOSAccepted);
            setPermissionsChecked(val.permissions.isUsagePermissionAccepted);
        } catch (err) {
            console.error(err);
        }
    }, []);

    const setPlaytimeOptions = useCallback(async () => {
        try {
            await Playtime.setPlaytimeOptions({ userId: USER_ID, sdkHash: SDK_HASH });
        } catch (err) {
            console.error(err);
        }

        setOptionsAreSet(true);
    }, []);

    const setUpPlaytime = useCallback(async () => {
        await setPlaytimeOptions();
        await checkUsagePermission();
    }, [checkUsagePermission, setPlaytimeOptions]);

    const fetchCampaigns = useCallback(async () => {
        if (tosChecked && permissionsChecked) {
            try {
                const apps = await PlaytimeStudio.getCampaigns({ userId: USER_ID, sdkHash: SDK_HASH });
                const installedApps = await PlaytimeStudio.getInstalledCampaigns({ userId: USER_ID, sdkHash: SDK_HASH });
                setCatalogCampaigns(apps.campaigns);
                setInstalledCampaigns(installedApps.campaigns);
            } catch (err) {
                console.error(err);
            }
        }
    }, [tosChecked, permissionsChecked]);

    const openChatbot = useCallback(() => {
        try {
            PlaytimeStudio.openChatbot(undefined);
        } catch (err) {
            console.error(err);
        }
    }, []);

    const deeplinkInstalledApps = useCallback(() => {
        try {
            PlaytimeStudio.showInstalledApps();
        } catch (err) {
            console.error(err);
        }
    }, []);

    useEffect(() => {
        fetchCampaigns();
    }, [fetchCampaigns]);

    useEffect(() => {
        setUpPlaytime();
    }, []);

    return {
        loading,
        activeTab,
        tosChecked,
        openChatbot,
        setActiveTab,
        setTosChecked,
        catalogCampaigns,
        installedCampaigns,
        permissionsChecked,
        setPermissionsChecked,
        clickedCampaign,
        setClickedCampaign,
        deeplinkInstalledApps,
    };
};
