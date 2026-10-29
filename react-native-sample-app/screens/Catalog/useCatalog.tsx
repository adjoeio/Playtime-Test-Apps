import { useCallback, useEffect, useMemo, useState } from 'react';
import Playtime, { AppCategory, PlaytimeCampaign, PlaytimeStudio } from 'react-native-adjoe-sdk';
import { SDK_HASH, USER_ID } from '../../util/constants';
import { Platform } from 'react-native';
import { requestTrackingPermission } from 'react-native-tracking-transparency';

export const useCatalog = () => {
    const [refreshing, setRefreshing] = useState<boolean>(false);
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
            try {
                const val = await Playtime.getStatus();
                setTosChecked(val.isTosAccepted);
            } catch (err) {
                console.error(err);
            }

            // Permissions are fetched using an external library
            const trackingStatus = await requestTrackingPermission();
            if (trackingStatus === 'authorized' || trackingStatus === 'unavailable') {
                setPermissionsChecked(true);
            } else {
                setPermissionsChecked(false);
            }
            return null;
        }

        try {
            const val = await Playtime.getStatus();
            setTosChecked(val.isTosAccepted);
            setPermissionsChecked(val.isUsagePermissionAccepted || false);
        } catch (err) {
            console.error(err);
        }
    }, []);

    const initPlaytime = useCallback(async () => {
        try {
            await Playtime.initPlaytime({ userId: USER_ID, sdkHash: SDK_HASH });
        } catch (err) {
            console.error(err);
        }

        setOptionsAreSet(true);
    }, []);

    const setUpPlaytime = useCallback(async () => {
        await initPlaytime();
        await checkUsagePermission();
    }, [checkUsagePermission, initPlaytime]);

    const fetchCampaigns = useCallback(async () => {
        if (tosChecked && permissionsChecked) {
            try {
                const apps = await PlaytimeStudio.getCampaigns({ category: AppCategory.BOTH });
                const installedApps = await PlaytimeStudio.getInstalledCampaigns({ category: AppCategory.BOTH });
                setCatalogCampaigns(apps.campaigns);
                setInstalledCampaigns(installedApps.campaigns);
            } catch (err) {
                console.error(err);
            }
        }
    }, [tosChecked, permissionsChecked]);

    const onRefresh = useCallback(async () => {
        setRefreshing(true);
        await fetchCampaigns();
        setRefreshing(false);
    }, [fetchCampaigns]);

    const openChatbot = useCallback(() => {
        try {
            PlaytimeStudio.openChatbot(undefined);
        } catch (err) {
            console.error(err);
        }
    }, []);

    const deeplinkInstalledApps = useCallback(() => {
        try {
            PlaytimeStudio.showInstalledCampaigns();
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
        refreshing,
        onRefresh,
    };
};
